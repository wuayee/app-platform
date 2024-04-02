# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：Fit远程调用Protobuf序列化工具
"""
from fitframework.core.network.proto import converter_register


class _ProtobufSerializer(object):

    def from_return_value_protobuf(self, return_type: type, return_value) -> bytes:
        """ 服务端根据function定义和返回结果转bytestring，用于响应远程通讯请求 """
        if return_type is None:
            return b''
        proto_converter = converter_register.get_converter_by_entity_type(return_type)
        return proto_converter.to_message(return_value, return_type).SerializeToString()

    def to_return_value_protobuf(self, return_type: type, return_data: bytes):
        """ 客户端根据function定义和FitResponse bytestring转返回结果，用于获得远程通讯请求结果 """
        if return_type:
            proto_converter = converter_register.get_converter_by_entity_type(return_type)
            return proto_converter.from_message(proto_converter.parse_message(return_data), return_type)
        return None


class _NullArguments(object):
    def __init__(self, data: bytes = None, size: int = 0):
        self._data = data if data else bytearray(_NullArguments._get_byte_size(size))

    @staticmethod
    def _get_byte_size(size: int):
        return (size // 8) if size % 8 == 0 else (size // 8 + 1)

    def mark_null(self, pos: int):
        if isinstance(self._data, bytes):  # read-only mode
            return
        self._data[pos // 8] = self._data[pos // 8] | (1 << pos % 8)

    def is_null(self, pos: int) -> bool:
        return self._data[pos // 8] & (1 << pos % 8) > 0

    def as_bytes(self) -> bytes:
        return bytes(self._data)
