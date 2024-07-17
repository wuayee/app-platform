# coding: utf-8
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
""" `databus.client.SocketClient`类型, 连接DataBus的socket线程

本文件内容为DataBus Python SDK的`databus.client.SocketClient`类实现.
`SocketClient`类提供连接到DataBus内核的socket通道.
"""
import concurrent.futures
import logging
import queue
import socket
from contextlib import contextmanager
from typing import Optional, Tuple, Union, Dict

import flatbuffers
from databus.dto import WriteRequest, ReadRequest
from databus.exceptions import CoreError
from databus.manager import MessageSeqManager, ResponseItem, ResponseManager
from databus.message import (
    MessageHeader, MAX_MESSAGE_LENGTH, MESSAGE_RESPONSE_MAPPING,
    ApplyMemoryMessage, ApplyMemoryMessageResponse,
    ApplyPermissionMessage,
    ReleaseMemoryMessage, ReleasePermissionMessage,
    GetMetaDataMessage, GetMetaDataMessageResponse,
    CoreMessageType, CorePermissionType, CoreMessageResponseTypeHint, DataBusErrorCode
)


class SocketClient:
    """
    和DataBus内核建立socket连接并对外提供消息发送接口的client
    """
    # 最长消息等待时间(unit: seconds)
    MAX_MESSAGE_WAITING_TIME = 30.0

    def __init__(self, core_address: Tuple[str, int] = None, core_socket: Optional[socket.socket] = None):
        """建立与`core_address` = (`core_host`, `core_port`)的socket连接并保持
        或者直接使用core_socket, 二选一

        :param core_address: DataBus core的(地址, 端口)
        :param core_socket: 连接DataBus core的socket(可选)
        """
        super().__init__()
        self._executor, self._socket, self._response_manager = None, None, None
        if core_socket is not None:
            self._socket = core_socket
        elif core_address is not None:
            self._host = socket.gethostbyname(core_address[0])
            self._port = core_address[1]
            self._socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self._socket.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, True)
            self._socket.connect((self._host, self._port))
        else:
            raise ValueError("SocketClient init without input parameters")

        self._executor = concurrent.futures.ThreadPoolExecutor(max_workers=1)
        self._mailbox: Dict[int, queue.Queue[ResponseItem]] = dict()
        self._seq_manager = MessageSeqManager()
        self._response_manager = ResponseManager(self._socket, self._mailbox)
        self._response_manager.start()

    def __del__(self):
        if self._executor:
            self._executor.shutdown()
            self._executor = None
        if self._socket:
            try:
                self._socket.shutdown(socket.SHUT_RDWR)
                self._socket.close()
            except Exception:
                # 忽略因为关闭步骤中对一个已破损socket做操作等异常，此处只期待socket关闭过程执行
                logging.debug("SocketClient close socket error, this can be ignored.")
            finally:
                self._socket = None
        if self._response_manager:
            self._response_manager.join()
            self._response_manager = None

    @staticmethod
    def _build_apply_permission_message(memory_id, permission_type, request):
        apply_builder = flatbuffers.Builder(MAX_MESSAGE_LENGTH)
        apply_builder.ForceDefaults(True)
        # 判断是否需要写入用户自定义数据
        need_to_write_user_data = permission_type == CorePermissionType.Write and request.is_operating_user_data
        user_data_vec = apply_builder.CreateByteVector(request.user_data) if need_to_write_user_data else None
        if not memory_id:
            user_key_str = apply_builder.CreateString(request.user_key)
            ApplyPermissionMessage.ApplyPermissionMessageStart(apply_builder)
            ApplyPermissionMessage.AddObjectKey(apply_builder, user_key_str)
        else:
            ApplyPermissionMessage.ApplyPermissionMessageStart(apply_builder)
            ApplyPermissionMessage.AddMemoryKey(apply_builder, memory_id)
        ApplyPermissionMessage.AddPermission(apply_builder, permission_type)
        if need_to_write_user_data:
            ApplyPermissionMessage.AddIsOperatingUserData(apply_builder, True)
            ApplyPermissionMessage.AddUserData(apply_builder, user_data_vec)
        apply_builder.Finish(ApplyPermissionMessage.ApplyPermissionMessageEnd(apply_builder))
        return apply_builder

    @staticmethod
    def _build_release_permission_message(memory_id, permission_type, request):
        release_builder = flatbuffers.Builder(MAX_MESSAGE_LENGTH)
        release_builder.ForceDefaults(True)
        if not memory_id:
            user_key_str = release_builder.CreateString(request.user_key)
            ReleasePermissionMessage.ReleasePermissionMessageStart(release_builder)
            ReleasePermissionMessage.AddObjectKey(release_builder, user_key_str)
        else:
            ReleasePermissionMessage.ReleasePermissionMessageStart(release_builder)
            ReleasePermissionMessage.AddMemoryKey(release_builder, memory_id)
        ReleasePermissionMessage.AddPermission(release_builder, permission_type)
        release_builder.Finish(ReleasePermissionMessage.ReleasePermissionMessageEnd(release_builder))
        return release_builder

    def send_hello_message(self) -> bool:
        """向DataBus内核发送健康检测消息

        :return: 内核返回的健康检测结果
        """
        message_builder = flatbuffers.Builder(MAX_MESSAGE_LENGTH)
        message_builder.ForceDefaults(True)
        message_seq, message_type = self._seq_manager.get_seq(), CoreMessageType.Hello
        MessageHeader.Start(message_builder)
        MessageHeader.AddType(message_builder, message_type)
        MessageHeader.AddSize(message_builder, 0)
        MessageHeader.AddSeq(message_builder, message_seq)
        message_builder.Finish(MessageHeader.End(message_builder))
        message = message_builder.Output()
        return self._executor.submit(self._handle_message, message_seq, message, message_type).result()

    def send_shared_malloc_message(self, user_key: str, size: int) -> ApplyMemoryMessageResponse:
        """向DataBus内核发送申请内存块消息

        :param user_key: 要申请的内存块名
        :param size: 要申请的内存块大小
        :return: 内核返回的ApplyMemoryMessageResponse
        """
        builder = flatbuffers.Builder(MAX_MESSAGE_LENGTH)
        user_key_str = builder.CreateString(user_key)
        ApplyMemoryMessage.ApplyMemoryMessageStart(builder)
        ApplyMemoryMessage.AddObjectKey(builder, user_key_str)
        ApplyMemoryMessage.AddMemorySize(builder, size)
        builder.Finish(ApplyMemoryMessage.ApplyMemoryMessageEnd(builder))
        logging.info("Sending allocation request for %u bytes.", size)
        return self._send_message(builder, CoreMessageType.ApplyMemory)

    def send_shared_free_message(self, user_key: str = None, memory_id: Optional[int] = None):
        """向DataBus内核发送释放内存块消息

        :param user_key: 要释放的内存块名称, 与memory_id二选一
        :param memory_id: 要释放的内存块ID, 与user_key二选一
        """
        builder = flatbuffers.Builder(MAX_MESSAGE_LENGTH)
        # 优先使用memory_id
        if not memory_id:
            user_key_str = builder.CreateString(user_key)
            ReleaseMemoryMessage.ReleaseMemoryMessageStart(builder)
            ReleaseMemoryMessage.AddObjectKey(builder, user_key_str)
        else:
            # 否则使用user_key
            ReleaseMemoryMessage.ReleaseMemoryMessageStart(builder)
            ReleaseMemoryMessage.AddMemoryKey(builder, memory_id)
        builder.Finish(ReleaseMemoryMessage.ReleaseMemoryMessageEnd(builder))
        self._send_message(builder, CoreMessageType.ReleaseMemory)

    def send_get_meta_message(self, user_key: str) -> GetMetaDataMessageResponse:
        """向DataBus内核查询内存块相关元信息

        :param user_key: 要查询元信息的内存块名
        :return: 内核返回的GetMetaDataMessageResponse
        """
        builder = flatbuffers.Builder(MAX_MESSAGE_LENGTH)
        user_key_str = builder.CreateString(user_key)
        GetMetaDataMessage.Start(builder)
        GetMetaDataMessage.AddObjectKey(builder, user_key_str)
        builder.Finish(GetMetaDataMessage.End(builder))
        return self._send_message(builder, CoreMessageType.GetMetaData)

    @contextmanager
    def with_permission(self,
                        permission_type: CorePermissionType,
                        request: Union[WriteRequest, ReadRequest],
                        memory_id: Optional[int] = None):
        """返回一个自动申请释放内存块权限的context, 例:

        with socket_client.with_permission(permission_type, user_key="key") as (memory_id, memory_size):
            ... # do anything that requires permission application and release

        :param permission_type: 申请的权限类型
        :param request: 需要申请权限的请求体
        :param memory_id: 需要申请权限的内存块id, 若有则优先使用
        :raise CoreError: DataBus内核返回的内存申请失败错误
        """
        if not request.user_key and not memory_id:
            raise ValueError("Both are None: user_key and memory_id.")
        apply_builder = self._build_apply_permission_message(memory_id, permission_type, request)
        try:
            response = self._send_message(apply_builder, CoreMessageType.ApplyPermission)
            CoreError.check_core_response("apply permission failed, result code {}", response.ErrorType())
            # 自动实现context manager, 返回ApplyPermissionMessageResponse方便裸读写
            yield response
        finally:
            release_builder = self._build_release_permission_message(memory_id, permission_type, request)
            # 返回值为None, 忽略
            _ = self._send_message(release_builder, CoreMessageType.ReleasePermission)

    def _send_message(self, message_builder: flatbuffers.Builder,
                      message_type: CoreMessageType) -> Optional[CoreMessageResponseTypeHint]:
        """向DataBus内核发送消息, 自动为message_builder中的消息体添加完整消息头并发送

        :param message_builder: 要发送消息体的flatbuffers builder, 注意**需要已经Finished**
        :param message_type: 要发送的消息类型
        :return: 如果内核有返回则返回, 否则返回None
        """
        body_size = message_builder.Offset()
        message_seq = self._seq_manager.get_seq()
        MessageHeader.Start(message_builder)
        MessageHeader.AddType(message_builder, message_type)
        MessageHeader.AddSize(message_builder, body_size)
        MessageHeader.AddSeq(message_builder, message_seq)
        message_builder.Finish(MessageHeader.End(message_builder))
        message = message_builder.Output()
        return self._executor.submit(self._handle_message, message_seq, message, message_type).result()

    def _handle_message(
            self,
            message_seq: int,
            message: bytes,
            message_type: CoreMessageType) -> Optional[Union[bytes, bool]]:
        """发送消息, 并且如果等待response则返回response

        :param message_seq: 消息的序列号
        :param message: 需要发送的消息
        :param message_type: 发送的消息消息类型
        :raise CoreError: 内核返回错误
        :return: 如果消息有response则返回response, 否则返回None
        """
        logging.debug("DataBus message [seq=%d] to core: %s.", message_seq, message.hex())
        self._mailbox[message_seq] = queue.Queue(maxsize=1)
        try:
            self._socket.send(message)
        except Exception as e:
            raise CoreError("Error when sending message to core, code {}.", DataBusErrorCode.UnknownError) from e
        if message_type in MESSAGE_RESPONSE_MAPPING:
            logging.info("Sent message [seq=%d] to core", message_seq)
            mail = None
            try:
                mail = self._mailbox[message_seq].get(timeout=self.MAX_MESSAGE_WAITING_TIME)
            except queue.Empty:
                logging.info("Waiting message [seq=%d] reaches timeout.", message_seq)
            finally:
                del self._mailbox[message_seq]
            if not mail or not mail.message_type:
                raise CoreError("Received nothing from core, code {}.", DataBusErrorCode.UnknownError)
            if mail.message_type == CoreMessageType.Error:
                raise CoreError("Received error {} from core.", mail.response.ErrorType())
            return mail.response
        return None
