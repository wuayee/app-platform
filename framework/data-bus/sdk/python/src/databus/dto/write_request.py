# coding: utf-8
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
""" `databus.dto.WriteRequest`类型, 表示向DataBus内核发送写入请求"""

from dataclasses import dataclass

_MAX_USER_DATA_LEN = 1024


@dataclass
class WriteRequest:
    """向DataBus内核的发送写入请求"""
    # 期望写入的内存块名
    user_key: str
    # 写入的内容
    contents: bytes
    # 写入位置的偏移, 默认为0
    offset: int = 0
    # 是否需要写入用户自定义数据
    is_operating_user_data: bool = False
    # 用户自定义数据
    user_data: bytes = b""

    def validation(self):
        if not self.contents or self.offset < 0:
            raise ValueError("Invalid input parameter: {}.".format(
                f"contents is {self.contents}" if not self.contents else f"offset is {self.offset}"
            ))
        if self.is_operating_user_data and len(self.user_data) > _MAX_USER_DATA_LEN:
            raise ValueError("User data len {} exceed MAX len: {}".format(len(self.user_data), _MAX_USER_DATA_LEN))
