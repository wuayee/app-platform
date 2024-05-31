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

    def read_once(self, user_key: str, size: Optional[int] = None, offset: int = 0) -> Optional[bytes]:
        """读取user_key的内存, 从offset开始读取size大小

        :param user_key: 期望读取的内存块名
        :param size:  读取的大小, 若为None则读取全部
        :param offset: 读取开始偏移, 默认为0
        :return: 内容
        """
        self._pre_access_check(user_key)
        if offset < 0 or (size and size <= 0):
            raise ValueError("Invalid input parameter: {}.".format(
                f"offset is {offset}" if offset < 0 else f"size is {size}"
            ))
        with self._socket.with_permission(
                CorePermissionType.Read, user_key=user_key,
                memory_id=self._mem_manager.to_memory_id(user_key)) as (memory_id, memory_size):
            if size and size + offset > memory_size:
                logging.warning("Invalid access to memory @ id=[%d] memory_size=[%d], where read_size=[%d] offset=[%d]",
                                memory_id, memory_size, size, offset)
                return None
            size = size if size else memory_size
            logging.info("ready to read memory @ id=[%d] size=[%d/%d] offset=[%d]", memory_id, size, memory_size,
                         offset)
            contents = shared_mem_read(memory_id, size, offset=offset)
        return contents

    def write_once(self, user_key: str, contents: bytes, offset: int = 0) -> int:
        """写入user_key的内存, 从offset开始写入contents的全部内容

        :param user_key: 期望写入的内存块名
        :param contents: 写入的内容
        :param offset: 写入位置的偏移, 默认为0
        :return: 写入长度
        """
        self._pre_access_check(user_key)
        if not contents or offset < 0:
            raise ValueError("Invalid input parameter: {}.".format(
                f"contents is {contents}" if not contents else f"offset is {offset}"
            ))
        with self._socket.with_permission(
                CorePermissionType.Write, user_key=user_key,
                memory_id=self._mem_manager.to_memory_id(user_key)) as (memory_id, memory_size):
            if len(contents) + offset > memory_size:
                logging.warning("Invalid access to memory @ id=[%d] memory_size=[%d], where write_len=[%d] offset=[%d]",
                                memory_id, memory_size, len(contents), offset)
                return 0
            logging.info("ready to write memory @ id=[%d] size=[%d] offset=[%d]", memory_id, len(contents), offset)
            write_len = shared_mem_write(memory_id, contents, offset=offset)
        return write_len

    def _pre_access_check(self, user_key: str):
        """ 检查对内存的访问的前置条件 """
        if not self.is_connected():
            raise NotConnectedError()
        if not user_key:
            raise ValueError("Invalid input parameter, user_key is None")
