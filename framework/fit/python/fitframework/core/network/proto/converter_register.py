# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：protobuf converter注册和获取
"""
import importlib
from typing import Any, get_origin

import fitframework.utils.serialize_utils
from fitframework.api.logging import fit_logger
from fitframework.core.network.proto import buildin_converters
from fitframework.core.network.proto.message_converter import MessageConverter

_entity_instance_dict = {}
_message_entity_set = set()


class ConverterMeta(type):
    def __new__(mcs, class_name, parents, attrs):
        cls = super(ConverterMeta, mcs).__new__(mcs, class_name, parents, attrs)
        _register_message_converter(cls, attrs['_proto_type'])
        cls._init_params = fitframework.utils.serialize_utils.get_parameter_types(cls, with_name=True)
        del attrs['_proto_type']
        return cls

    def __call__(cls, *args, **kwargs):
        return super(ConverterMeta, cls).__call__(*args, **kwargs)


def get_converter_by_entity_type(entity_type):
    """ 根据python注册对象类型获得对应converter """
    if get_origin(entity_type) is not None:
        if get_origin(entity_type).__name__ == 'list':
            return _entity_instance_dict[list]
        if get_origin(entity_type).__name__ == 'dict':
            return _entity_instance_dict[dict]
    return _entity_instance_dict[entity_type]


def get_proto_type_by_entity_type(entity_type):
    """ 根据python注册对象类型获得对应protobuf对象类型 """
    if get_origin(entity_type) is not None:
        if get_origin(entity_type).__name__ == 'list':
            return _entity_instance_dict[list]
        if get_origin(entity_type).__name__ == 'dict':
            return _entity_instance_dict[dict]
    return _entity_instance_dict[entity_type]


def is_proto_message(entity_type):
    return entity_type in _message_entity_set


def get_class(module_name, class_name):
    import sys
    return getattr(sys.modules.get(module_name), class_name)


def _register_converter(entity_type, proto_type, cls: Any = buildin_converters.ValueConverter):
    fit_logger.info(f"register converter for structure: {entity_type} and proto: {proto_type}")
    _entity_instance_dict[entity_type] = cls(proto_type)


def _register_message_converter(entity_type, proto_type_full_name):
    _message_entity_set.add(entity_type)
    proto_type = _full_name_to_class(proto_type_full_name)
    _register_converter(entity_type, proto_type, MessageConverter)


def _full_name_to_class(full_name: str):
    border = full_name.rindex('.')
    module_name = full_name[:border]
    class_name = full_name[border + 1:]
    module = importlib.import_module(module_name)
    return getattr(module, class_name)
