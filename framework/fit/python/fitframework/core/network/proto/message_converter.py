# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：Protobuf针对Fit方法入参出参转换 - 公共结构体参数类型
"""
from enum import Enum

_FIT_ANY_TYPE_PREFIX = 'type.fit.com/'


class AnyConverter(object):
    """ 泛型结构体转换基类 """


class MessageConverter(AnyConverter):
    def __init__(self, proto_type):
        self.proto_type = proto_type

    def parse_message(self, serialized_data: bytes):
        return self.proto_type.FromString(serialized_data)


def _is_proto_message(entity_type) -> bool:
    from fitframework.core.network.proto import converter_register
    return converter_register.is_proto_message(entity_type)


def _from_any_message(proto, generic_type):
    """ FitAny对象转python对象 """
    from fitframework.core.network.proto import converter_register
    converter = converter_register.get_converter_by_entity_type(generic_type)
    return converter.from_message(converter.parse_message(proto.value), generic_type)


class AttributeEnum(bytes, Enum):
    def __new__(cls, value, set_attribute_func, get_attribute_func):
        obj = bytes.__new__(cls, [value])
        obj._value_ = value
        obj.set_func = set_attribute_func
        obj.get_func = get_attribute_func
        return obj


class _PassOnGenericType:
    """
    struct_type和parent_type都为泛型。parent代表struct的父结构体。
    此类将parent_type中的具化的泛型类型传递给struct_type，用完后恢复struct_type
    """

    def __init__(self, struct_type, parent_type):
        self.pass_on = _PassOnGenericType.pass_on(struct_type, parent_type)
        if self.pass_on:
            self.struct_type = struct_type
            self.parent_type = parent_type

    def __enter__(self):
        if self.pass_on:
            self.current_args = self.struct_type.__args__
            self.struct_type.__args__ = self.parent_type.__args__

    def __exit__(self, exc_type, exc_val, exc_tb):
        if self.pass_on:
            self.struct_type.__args__ = self.current_args
