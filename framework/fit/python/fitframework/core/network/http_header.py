# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
"""
功 能：HTTP 请求头与响应头相关工具。
"""
from enum import Enum


class HttpHeader(Enum):
    FORMAT = "FIT-Data-Format"
    GENERICABLE_VERSION = "FIT-Genericable-Version"
    DEGRADABLE = "FIT-Degradable"
    CODE = "FIT-Code"
    MESSAGE = "FIT-Message"
    TLV = "FIT-TLV"
