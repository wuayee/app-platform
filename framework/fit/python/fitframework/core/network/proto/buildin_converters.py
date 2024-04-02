# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：Protobuf针对Fit方法入参出参转换 - 内建参数类型
"""


class ValueConverter(object):
    """ 基础数据类型通用转换 """

    def __init__(self, proto_type):
        self.proto_type = proto_type

    def to_message(self, entity, _=None):
        return self.proto_type(value=entity)

    def from_message(self, proto, _=None):
        return proto.value

    def parse_message(self, data: bytes):
        return self.proto_type.FromString(data)


class ListConverter(object):
    """ 列表转换 """

    def __init__(self, proto_type):
        self.proto_type = proto_type

    def to_message(self, entity, struct_type):
        list_message = self.proto_type()
        return list_message

    def from_message(self, pb_list, struct_type):
        list_data = []
        return list_data

    def parse_message(self, data: bytes):
        return self.proto_type.FromString(data)


class DictConverter(object):
    """ 字典转换 """

    def __init__(self, proto_type):
        self.proto_type = proto_type

    def to_message(self, entity, struct_type):
        dict_message = self.proto_type()
        return dict_message

    def from_message(self, pb_dict, struct_type):
        map_data = {}
        return map_data

    def parse_message(self, serialized_data: bytes):
        return self.proto_type.FromString(serialized_data)
