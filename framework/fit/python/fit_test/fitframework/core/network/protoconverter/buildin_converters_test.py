# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：buildin_converters单元测试
"""
import unittest
from typing import List, Dict

import numpy

from fitframework.core.network.fit_response import FitResponse
from fitframework.core.network.fit_response import FitResponse as StructResponse
from fitframework.core.network.proto import converter_register
from fitframework.core.network.proto.broker_pb2 import FitResponse as PbResponse


class _FitResponseConverter(object):
    def __init__(self, proto_type):
        self._proto_type = proto_type

    def to_message(self, entity: StructResponse, _=None):
        return PbResponse(code=entity.code, message=entity.message, data=entity.data)

    def from_message(self, proto: PbResponse, _=None):
        return StructResponse(proto.data, proto.code, proto.message)

    def parse_message(self, data: bytes):
        return self._proto_type.FromString(data)


class MessageConverterTestCase(unittest.TestCase):
    def test_str(self):
        instance = converter_register.get_converter_by_entity_type(str)
        proto_message = instance.to_message('_test')
        proto_bytes = proto_message.SerializeToString()
        self.assertEqual('_test', instance.from_message(instance.parse_message(proto_bytes)))

    def test_bool(self):
        instance = converter_register.get_converter_by_entity_type(bool)
        proto_message = instance.to_message(True)
        proto_bytes = proto_message.SerializeToString()
        self.assertEqual(True, instance.from_message(instance.parse_message(proto_bytes)))

    def test_int32(self):
        instance = converter_register.get_converter_by_entity_type(numpy.int32)
        proto_message = instance.to_message(1)
        proto_bytes = proto_message.SerializeToString()
        self.assertEqual(1, instance.from_message(instance.parse_message(proto_bytes)))

    def test_int64(self):
        instance = converter_register.get_converter_by_entity_type(numpy.int64)
        proto_message = instance.to_message(1)
        proto_bytes = proto_message.SerializeToString()
        self.assertEqual(1, instance.from_message(instance.parse_message(proto_bytes)))

    def test_float(self):
        instance = converter_register.get_converter_by_entity_type(numpy.float32)
        proto_message = instance.to_message(1.10)
        proto_bytes = proto_message.SerializeToString()
        self.assertAlmostEqual(1.1, instance.from_message(instance.parse_message(proto_bytes)))

    def test_double(self):
        instance = converter_register.get_converter_by_entity_type(numpy.float64)
        proto_message = instance.to_message(1.1111)
        proto_bytes = proto_message.SerializeToString()
        self.assertAlmostEqual(1.1111, instance.from_message(instance.parse_message(proto_bytes)))

    def test_uint32(self):
        instance = converter_register.get_converter_by_entity_type(numpy.uint32)
        proto_message = instance.to_message(1)
        proto_bytes = proto_message.SerializeToString()
        self.assertEqual(1, instance.from_message(instance.parse_message(proto_bytes)))

    def test_uint64(self):
        instance = converter_register.get_converter_by_entity_type(numpy.uint64)
        proto_message = instance.to_message(1)
        proto_bytes = proto_message.SerializeToString()
        self.assertEqual(1, instance.from_message(instance.parse_message(proto_bytes)))

    def test_bytes(self):
        instance = converter_register.get_converter_by_entity_type(bytes)
        proto_message = instance.to_message(b'\x01\x02')
        proto_bytes = proto_message.SerializeToString()
        self.assertEqual(b'\x01\x02', instance.from_message(instance.parse_message(proto_bytes)))

    def test_list_primary(self):
        list_type = _get_list_type_primary()
        instance = converter_register.get_converter_by_entity_type(list_type)
        list_value = [1, 2, 3]
        proto_message = instance.to_message(list_value, list_type)
        proto_bytes = proto_message.SerializeToString()
        self.assertListEqual(list_value, instance.from_message(instance.parse_message(proto_bytes),
                                                               list_type))

    def test_list_struct(self):
        list_type = _get_list_type_struct()
        converter_register._register_converter(StructResponse, PbResponse, _FitResponseConverter)
        instance = converter_register.get_converter_by_entity_type(list_type)
        list_value = [FitResponse(b'\x01\x02', 1, '1'), FitResponse(b'\x03\x04', 2, '2')]
        proto_message = instance.to_message(list_value, list_type)
        proto_bytes = proto_message.SerializeToString()
        self.assertListEqual(list_value, instance.from_message(instance.parse_message(proto_bytes),
                                                               list_type))

    def test_dict_primary(self):
        dict_type = _get_dict_type_primary()
        instance = converter_register.get_converter_by_entity_type(dict_type)
        dict_value = {'a': 1, 'b': 2, 'c': 3}
        proto_message = instance.to_message(dict_value, dict_type)
        proto_bytes = proto_message.SerializeToString()
        self.assertDictEqual(dict_value, instance.from_message(instance.parse_message(proto_bytes),
                                                               dict_type))

    def test_dict_struct(self):
        dict_type = _get_dict_type_struct()
        converter_register._register_converter(StructResponse, PbResponse, _FitResponseConverter)
        instance = converter_register.get_converter_by_entity_type(dict_type)
        dict_value = {'a': FitResponse(b'\x01\x02', 1, '1'), 'b': FitResponse(b'\x03\x04', 2, '2')}
        proto_message = instance.to_message(dict_value, dict_type)
        proto_bytes = proto_message.SerializeToString()
        self.assertDictEqual(dict_value, instance.from_message(instance.parse_message(proto_bytes),
                                                               dict_type))


def _get_list_type_primary():
    def func() -> List[numpy.int32]:
        pass
    import inspect
    sig = inspect.signature(func)
    return sig.return_annotation


def _get_list_type_struct():
    def func() -> List[FitResponse]:
        pass
    import inspect
    sig = inspect.signature(func)
    return sig.return_annotation


def _get_dict_type_primary():
    def func() -> Dict[str, numpy.int32]:
        pass
    import inspect
    sig = inspect.signature(func)
    return sig.return_annotation


def _get_dict_type_struct():
    def func() -> Dict[str, FitResponse]:
        pass
    import inspect
    sig = inspect.signature(func)
    return sig.return_annotation
