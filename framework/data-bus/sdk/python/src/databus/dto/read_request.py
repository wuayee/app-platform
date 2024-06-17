# coding: utf-8
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
""" `databus.dto.ReadRequest`类型, 表示向DataBus内核发送读取请求"""
from typing import Optional


class ReadRequest:
    """向DataBus内核的发送读取请求"""
    # 期望读取的内存块名
    user_key: str
    # 读取的大小
    size: Optional[int]
    # 读取位置的偏移, 默认为0
    offset: int = 0
    # 是否需要读取用户自定义数据
    is_operating_user_data: bool = False

    def __init__(self, user_key: str, size: Optional[int] = None, offset: int = 0,
                 is_operating_user_data: bool = False):
        """向DataBus内核的发送读取请求

        :param user_key: 期望读取的内存块名
        :param size: 读取的长度
        :param offset: 读取位置的偏移, 默认为0
        :param is_operating_user_data: 是否读取用户自定义数据
        """
        self.user_key = user_key
        self.size = size
        self.offset = offset
        self.is_operating_user_data = is_operating_user_data

    def validation(self):
        if self.offset < 0 or (self.size and self.size < 0):
            raise ValueError("Invalid input parameter: {}.".format(
                f"offset is {self.offset}" if self.offset < 0 else f"size is {self.size}"
            ))
