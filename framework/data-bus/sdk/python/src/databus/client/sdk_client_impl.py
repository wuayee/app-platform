# coding: utf-8
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
""" `databus.client.SdkClient`类型, 连接DataBus的接口实现类.

本文件内容为DataBus Python SDK的`databus.client.SdkClient`类的实现.
`SdkClientImpl`类提供连接到DataBus内核的各种功能.
"""
import logging
from typing import Optional, Tuple

from databus.memory_io import read as shared_mem_read, write as shared_mem_write

from databus.exceptions import CoreError, NotConnectedError
from databus.manager import MemoryManager
from databus.message import CorePermissionType
from databus.dto import ReadRequest, WriteRequest, ReadResponse
from .socket_client import SocketClient


class SdkClientImpl:
    """
    提供给外部使用的DataBus Python SDK client类, 提供DataBus SDK标准的内存访问方法
    """
    __slots__ = ("_socket", "_mem_manager")

    def __init__(self):
        self._socket: Optional[SocketClient] = None
        self._mem_manager = MemoryManager()

    def open(self, core_host: str, core_port: int):
        """关闭现有连接, 连接到新地址(core_host, core_port), 注意会**重置共享内存信息**

        :param core_host: DataBus内核地址
        :param core_port: DataBus内核监听端口
        """
        if self.is_connected():
            self.close()
        self._socket = SocketClient((core_host, core_port))
        self._mem_manager.reset()

    def open_with_socket(self, core_socket: SocketClient = None):
        """关闭现有连接, 并使用core_socket作为连接, 注意会**重置共享内存信息**

        :param core_socket: 连接到DataBus内核的socket连接
        """
        if self.is_connected():
            self.close()
        self._socket = core_socket
        self._mem_manager.reset()

    def close(self):
        """ 关闭现有连接 """
        del self._socket
        self._socket = None

    def is_connected(self) -> bool:
        """ 判断是否已有到DataBus内核的socket连接, 注意**不保证连接通** """
        return self._socket is not None

    def shared_malloc(self, user_key: str, size: int):
        """分配大小为size的内存, 并设定内存块名为user_key

        :param user_key: 内存块名
        :param size: 期望分配的内存大小
        """
        self._pre_access_check(user_key)
        if size <= 0:
            raise ValueError(f"Invalid input parameter: size is {size}")
        response = self._socket.send_shared_malloc_message(user_key, size)
        CoreError.check_core_response("Allocation failed with error code {}", response.ErrorType())
        self._mem_manager.add_memory_block(user_key=user_key, memory_id=response.MemoryKey(),
                                           memory_size=response.MemorySize())

    def shared_free(self, user_key: str):
        """通知DataBus内核释放user_key对应的内存块

        :param user_key: 期望释放的内存块名
        """
        self._pre_access_check(user_key)
        self._socket.send_shared_free_message(user_key=user_key, memory_id=self._mem_manager.to_memory_id(user_key))
        self._mem_manager.del_memory_block(user_key)

    def read_once(self, request: ReadRequest) -> ReadResponse:
        """读取request.user_key的内存, 从request.offset开始读取request.size大小

        另外, 如果request.is_operating_user_data为True, 则从DataBus内核中读取request.user_key的内存的user_data

        :param request: 向DataBus读取的请求
        :return: ReadResponse(contents: Optional[bytes], user_data: Optional[bytes] = None)
        """
        self._pre_access_check(request.user_key)
        request.validation()
        user_data = None
        with self._socket.with_permission(
                CorePermissionType.Read, request=request,
                memory_id=self._mem_manager.to_memory_id(request.user_key)) as apply_response:
            if request.size and request.size + request.offset > apply_response.MemorySize():
                logging.warning("Invalid access to memory @ id=[%d] memory_size=[%d], where read_size=[%d] offset=[%d]",
                                apply_response.MemoryKey(), apply_response.MemorySize(), request.size, request.offset)
                return ReadResponse(None, None)
            size = request.size if request.size else apply_response.MemorySize()
            logging.info("ready to read memory @ id=[%d] size=[%d/%d] offset=[%d]",
                         apply_response.MemoryKey(), size, apply_response.MemorySize(),
                         request.offset)
            contents = shared_mem_read(apply_response.MemoryKey(), size, offset=request.offset)
            if request.is_operating_user_data:
                user_data = bytes([apply_response.UserData(i) for i in range(apply_response.UserDataLength())])
        return ReadResponse(contents, user_data)

    def write_once(self, request: WriteRequest) -> bool:
        """写入request.user_key的内存, 从request.offset开始写入request.contents的全部内容

        另外, 如果request.is_operating_user_data为True, 则向DataBus内核中为request.user_key的内存记录request.user_data

        :param request: 向DataBus写入的请求
        :return: 是否写入成功
        """
        self._pre_access_check(request.user_key)
        request.validation()
        with self._socket.with_permission(
                CorePermissionType.Write, request=request,
                memory_id=self._mem_manager.to_memory_id(request.user_key)) as apply_response:
            if len(request.contents) + request.offset > apply_response.MemorySize():
                logging.warning("Invalid access to memory @ id=[%d] memory_size=[%d], where write_len=[%d] offset=[%d]",
                                apply_response.MemoryKey(), apply_response.MemorySize(), len(request.contents),
                                request.offset)
                return False
            logging.info("ready to write memory @ id=[%d] size=[%d] offset=[%d]", apply_response.MemoryKey(),
                         len(request.contents), request.offset)
            write_len = shared_mem_write(apply_response.MemoryKey(), request.contents, offset=request.offset)
        return write_len != 0

    def get_meta_data(self, user_key: str) -> Tuple[int, bytes]:
        """从DataBus内核获取user_key内存的元数据

        :param user_key: 要获取元数据的内存名
        :return: (memory_size, user_data)的元组
        """
        self._pre_access_check(user_key)
        response = self._socket.send_get_meta_message(user_key)
        memory_id, memory_size = response.MemoryKey(), response.MemorySize()
        self._mem_manager.add_memory_block(user_key, memory_id, memory_size)
        data = bytes([response.UserData(i) for i in range(response.UserDataLength())])
        return memory_size, data

    def _pre_access_check(self, user_key: str):
        """ 检查对内存的访问的前置条件 """
        if not self.is_connected():
            raise NotConnectedError()
        if not user_key:
            raise ValueError("Invalid input parameter, user_key is None")
