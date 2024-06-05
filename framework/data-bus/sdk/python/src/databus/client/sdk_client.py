# coding: utf-8
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
import platform
from typing import Optional, Tuple

from databus.message import DataBusErrorCode
from databus.exceptions import CoreError, NotConnectedError
from databus.dto import ReadResponse, ReadRequest, WriteRequest
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

    def read_once(self, request: ReadRequest) -> Tuple[DataBusErrorCode, ReadResponse]:
        """读取request.user_key的内存, 从request.offset开始读取request.size大小

        另外, 如果request.is_operating_user_data为True, 则从DataBus内核中读取request.user_key的内存的user_data

        :param request: 向DataBus内核发送的读取请求
        :return: (错误码, ReadResponse)元组
        """
        try:
            return DataBusErrorCode.None_, self._impl.read_once(request)
        except CoreError as e:
            return e.error_code, ReadResponse(None, None)
        except NotConnectedError:
            return DataBusErrorCode.NotConnectedToDataBus, ReadResponse(None, None)
        except IOError:
            return DataBusErrorCode.MemoryReadError, ReadResponse(None, None)

    def write_once(self, request: WriteRequest) -> DataBusErrorCode:
        """写入request.user_key的内存, 从request.offset开始写入request.contents的全部内容

        另外, 如果request.is_operating_user_data为True, 则向DataBus内核中为request.user_key的内存记录request.user_data

        :param request: 向DataBus内核发送的写入请求
        :return: 写入操作错误码
        """
        try:
            success = self._impl.write_once(request)
            if not success:
                return DataBusErrorCode.IOOutOfBounds
            return DataBusErrorCode.None_
        except CoreError as e:
            return e.error_code
        except NotConnectedError:
            return DataBusErrorCode.NotConnectedToDataBus
        except IOError:
            return DataBusErrorCode.MemoryWriteError

    def get_meta_data(self, user_key: str) -> Tuple[DataBusErrorCode, Optional[Tuple[int, bytes]]]:

        """从DataBus内核获取user_key内存的元数据

        :param user_key: 要获取元数据的内存名
        :return: (memory_size, user_data)的元组或None
        """
        try:
            return DataBusErrorCode.None_, self._impl.get_meta_data(user_key=user_key)
        except CoreError as e:
            return e.error_code, None
        except NotConnectedError:
            return DataBusErrorCode.NotConnectedToDataBus, None
