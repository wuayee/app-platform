# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：message_converter单元测试
"""
import inspect
import unittest
from typing import List, Dict

from numpy import int32

from fit_test.fitframework.core.network.protoconverter.message_converter_sample import CodedData, \
    Person, EncapsulatedData, \
    EncapsulatedData2, EncapsulatedData3, EncapsulatedData4
from fit_test.fitframework.core.network.protoconverter.message_converter_sample import CodedData2
from fit_test.fitframework.core.network.protoconverter.message_converter_sample import CodedData3
from fitframework.core.network.proto import converter_register


def func1(num: int32) -> CodedData[Person]:
    pass


def func2(num: int32) -> CodedData[str]:
    pass


def func3(num: int32) -> CodedData[List[str]]:
    pass


def func4(num: int32) -> CodedData[Dict[str, str]]:
    pass


def func5(num: int32) -> CodedData2[str]:
    pass


def func6(num: int32) -> CodedData3[str]:
    pass


def func7(num: int32) -> EncapsulatedData[str]:
    pass


def func8(num: int32) -> EncapsulatedData2:
    pass


def func9(num: int32) -> EncapsulatedData3:
    pass


def func10(num: int32) -> EncapsulatedData4:
    pass


class GenericConverterTest(unittest.TestCase):
    def test_param_generic_struct(self):
        code_data_type = inspect.signature(func1).return_annotation
        test_data = CodedData(1, 'message', Person('Mike', 20, ['Mike', 'Mickael'], {'us': 'cal', 'eu': 'fra'}))
        converter = converter_register.get_converter_by_entity_type(CodedData)
        proto_message = converter.to_message(test_data, code_data_type)
        proto_bytes = proto_message.SerializeToString()
        result = converter.from_message(converter.parse_message(proto_bytes), code_data_type)
        self.assertEqual(test_data, result)

    def test_param_generic_raw_type(self):
        code_data_type = inspect.signature(func2).return_annotation
        test_data = CodedData(1, 'message', 'data')
        converter = converter_register.get_converter_by_entity_type(CodedData)
        proto_message = converter.to_message(test_data, code_data_type)
        proto_bytes = proto_message.SerializeToString()
        result = converter.from_message(converter.parse_message(proto_bytes), code_data_type)
        self.assertEqual(test_data.data, result.data)
        self.assertEqual(test_data.message, result.message)
        self.assertEqual(test_data.code, result.code)

    def test_param_generic_list_type(self):
        code_data_type = inspect.signature(func3).return_annotation
        test_data = CodedData(1, 'message', ['data1', 'data2', 'data3'])
        converter = converter_register.get_converter_by_entity_type(CodedData)
        proto_message = converter.to_message(test_data, code_data_type)
        proto_bytes = proto_message.SerializeToString()
        result = converter.from_message(converter.parse_message(proto_bytes), code_data_type)
        self.assertEqual(test_data, result)

    def test_param_generic_list_type_2(self):
        code_data_type = inspect.signature(func5).return_annotation
        test_data = CodedData2(1, 'message', ['data1', 'data2', 'data3'])
        converter = converter_register.get_converter_by_entity_type(CodedData2)
        proto_message = converter.to_message(test_data, code_data_type)
        proto_bytes = proto_message.SerializeToString()
        result = converter.from_message(converter.parse_message(proto_bytes), code_data_type)
        self.assertEqual(test_data, result)

    def test_param_generic_dict_type(self):
        code_data_type = inspect.signature(func4).return_annotation
        test_data = CodedData(1, 'message', {'key1': 'data1', 'key2': 'data2', 'key3': 'data3'})
        converter = converter_register.get_converter_by_entity_type(CodedData)
        proto_message = converter.to_message(test_data, code_data_type)
        proto_bytes = proto_message.SerializeToString()
        result = converter.from_message(converter.parse_message(proto_bytes), code_data_type)
        self.assertEqual(test_data, result)

    def test_param_generic_dict_type_2(self):
        code_data_type = inspect.signature(func6).return_annotation
        test_data = CodedData3(1, 'message', {'key1': 'data1', 'key2': 'data2', 'key3': 'data3'})
        converter = converter_register.get_converter_by_entity_type(CodedData3)
        proto_message = converter.to_message(test_data, code_data_type)
        proto_bytes = proto_message.SerializeToString()
        result = converter.from_message(converter.parse_message(proto_bytes), code_data_type)
        self.assertEqual(test_data, result)

    def test_param_generic_message_pass_on(self):
        code_data_type = inspect.signature(func7).return_annotation
        test_data = EncapsulatedData(1, CodedData(1, 'message', 'data'))
        converter = converter_register.get_converter_by_entity_type(EncapsulatedData)
        proto_message = converter.to_message(test_data, code_data_type)
        proto_bytes = proto_message.SerializeToString()
        result = converter.from_message(converter.parse_message(proto_bytes), code_data_type)
        self.assertEqual(test_data, result)

    def test_param_instantiate_message(self):
        code_data_type = inspect.signature(func8).return_annotation
        test_data = EncapsulatedData2(1, CodedData(1, 'message', 'data'))
        converter = converter_register.get_converter_by_entity_type(EncapsulatedData2)
        proto_message = converter.to_message(test_data, code_data_type)
        proto_bytes = proto_message.SerializeToString()
        result = converter.from_message(converter.parse_message(proto_bytes), code_data_type)
        self.assertEqual(test_data, result)

    def test_param_instantiate_message_2(self):
        code_data_type = inspect.signature(func9).return_annotation
        test_data = EncapsulatedData3(1, CodedData(1, 'message',
                                                   Person('Mike', 20, ['Mike', 'Mickael'], {'us': 'cal', 'eu': 'fra'})))
        converter = converter_register.get_converter_by_entity_type(EncapsulatedData3)
        proto_message = converter.to_message(test_data, code_data_type)
        proto_bytes = proto_message.SerializeToString()
        result = converter.from_message(converter.parse_message(proto_bytes), code_data_type)
        self.assertEqual(test_data, result)

    def test_param_instantiate_message_3(self):
        code_data_type = inspect.signature(func10).return_annotation
        test_data = EncapsulatedData4(1, CodedData(1, 'message', ['data1', 'data2']))
        converter = converter_register.get_converter_by_entity_type(EncapsulatedData4)
        proto_message = converter.to_message(test_data, code_data_type)
        proto_bytes = proto_message.SerializeToString()
        result = converter.from_message(converter.parse_message(proto_bytes), code_data_type)
        self.assertEqual(test_data, result)


if __name__ == '__main__':
    unittest.main()
