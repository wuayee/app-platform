# coding: utf-8
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
""" `databus.dto.ReadResponse`类型, 表示向DataBus内核发送读取请求的回信"""
from typing import Optional
from dataclasses import dataclass


@dataclass
class ReadResponse:
    """向DataBus内核发送读取请求的回信"""
    content: Optional[bytes]
    user_data: Optional[bytes] = None
