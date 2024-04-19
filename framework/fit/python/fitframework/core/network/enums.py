# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：通讯协议和规约类型枚举
"""
from enum import IntEnum


class ProtocolEnum(IntEnum):
    UNKNOWN = -1
    RSOCKET = 0,
    SOCKET = 1,
    HTTP = 2,
    GRPC = 3,
    HTTPS = 4,
    SHARE_MEMORY = 11
    UC = 12,


class SerializingStructureEnum(IntEnum):
    UNKNOWN = -1
    PROTOBUF = 0
    JSON = 1
    CBOR = 2


