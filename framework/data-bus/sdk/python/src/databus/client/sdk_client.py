# coding: utf-8
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
import platform
from typing import Optional, Tuple

from databus.message import DataBusErrorCode
from databus.exceptions import CoreError, NotConnectedError
from .sdk_client_impl import SdkClientImpl


class SdkClient:
    SUPPORTED_PLATFORMS = {"linux"}

    def __init__(self):
        self._impl = SdkClientImpl()

    def open(self, core_host: str, core_port: int) -> DataBusErrorCode:
        """关闭现有连接, 连接到新地址(core_host, core_port), 注意会**重置共享内存信息**

        :param core_host: DataBus内核地址
        :param core_port: DataBus内核监听端口
        :return 若平台不支持则返回PlatformNotSupported, 否则返回None_
        """
        if not core_host:
            raise ValueError(f"Invalid input parameter: core_host is {core_host}")
        if not core_port:
            raise ValueError(f"Invalid input parameter: core_port is {core_port}")
        if platform.system().lower() not in self.SUPPORTED_PLATFORMS:
            return DataBusErrorCode.PlatformNotSupported

        self._impl.open(core_host=core_host, core_port=core_port)
        return DataBusErrorCode.None_

    def close(self):
        """ 关闭现有连接 """
        self._impl.close()

    def is_connected(self) -> bool:
        """ 判断是否已有到DataBus内核的socket连接, 注意**不保证连接通** """
        return self._impl.is_connected()

    def shared_malloc(self, user_key: str, size: int) -> DataBusErrorCode:
        """分配大小为size的内存, 并设定内存块名为user_key

        :param user_key: 内存块名
        :param size: 期望分配的内存大小
        :return: 错误码
        """
        try:
            self._impl.shared_malloc(user_key, size)
        except CoreError as e:
            return e.error_code
        except NotConnectedError:
            return DataBusErrorCode.NotConnectedToDataBus
        return DataBusErrorCode.None_

    def shared_free(self, user_key: str) -> DataBusErrorCode:
        """通知DataBus内核释放user_key对应的内存块

        :param user_key: 期望释放的内存块名
        """
        try:
            self._impl.shared_free(user_key)
        except CoreError as e:
            return e.error_code
        except NotConnectedError:
            return DataBusErrorCode.NotConnectedToDataBus
        return DataBusErrorCode.None_

    def read_once(self, user_key: str, size: Optional[int] = None, offset: int = 0) \
            -> Tuple[DataBusErrorCode, Optional[bytes]]:
        """读取user_key的内存, 从offset开始读取size大小

        :param user_key: 期望读取的内存块名
        :param size:  读取的大小, 若为None则读取全部
        :param offset: 读取开始偏移, 默认为0
        :return: (错误码, 内容)元组
        """
        try:
            return DataBusErrorCode.None_, self._impl.read_once(user_key, size=size, offset=offset)
        except CoreError as e:
            return e.error_code, None
        except NotConnectedError:
            return DataBusErrorCode.NotConnectedToDataBus, None
        except IOError:
            return DataBusErrorCode.MemoryReadError, None

    def write_once(self, user_key: str, contents: bytes, offset: int = 0) -> DataBusErrorCode:
        """写入user_key的内存, 从offset开始写入contents的全部内容

        :param user_key: 期望写入的内存块名
        :param contents: 写入的内容
        :param offset: 写入位置的偏移, 默认为0
        :return: 写入操作错误码
        """
        try:
            write_len = self._impl.write_once(user_key, contents, offset=offset)
            if not write_len:
                return DataBusErrorCode.IOOutOfBounds
            return DataBusErrorCode.None_
        except CoreError as e:
            return e.error_code
        except NotConnectedError:
            return DataBusErrorCode.NotConnectedToDataBus
        except IOError:
            return DataBusErrorCode.MemoryWriteError
