# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：message_converter测试用公共结构体
"""
from typing import TypeVar, Generic, List, Dict

from numpy import int32

from fitframework.core.network.proto.converter_register import ConverterMeta

T = TypeVar('T')


class Person(metaclass=ConverterMeta):
    

    def __init__(self, name: str, age: int32, nickNames: List[str], addresses: Dict[str, str]):
        self.name = name
        self.age = age
        self.nickNames = nickNames
        self.addresses = addresses

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.__dict__ == other.__dict__

    def __hash__(self):
        return hash(tuple(self.__dict__.values()))

    def __repr__(self):
        return str(tuple(self.__dict__.values()))


class CodedData(Generic[T]):
    

    def __init__(self, code: int, message: str, data: T):
        self.code = code
        self.message = message
        self.data = data

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.__dict__ == other.__dict__

    def __hash__(self):
        return hash(tuple(self.__dict__.values()))

    def __repr__(self):
        return str(tuple(self.__dict__.values()))


class CodedData2(Generic[T]):
    

    def __init__(self, code: int, message: str, data: List[T]):
        self.code = code
        self.message = message
        self.data = data

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.__dict__ == other.__dict__

    def __hash__(self):
        return hash(tuple(self.__dict__.values()))

    def __repr__(self):
        return str(tuple(self.__dict__.values()))


class CodedData3(Generic[T]):
    

    def __init__(self, code: int, message: str, data: Dict[str, T]):
        self.code = code
        self.message = message
        self.data = data

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.__dict__ == other.__dict__

    def __hash__(self):
        return hash(tuple(self.__dict__.values()))

    def __repr__(self):
        return str(tuple(self.__dict__.values()))


class EncapsulatedData(Generic[T]):
    

    def __init__(self, name: int32, codedData: CodedData[T]):
        self.name = name
        self.codedData = codedData

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.__dict__ == other.__dict__

    def __hash__(self):
        return hash(tuple(self.__dict__.values()))

    def __repr__(self):
        return str(tuple(self.__dict__.values()))


class EncapsulatedData2(metaclass=ConverterMeta):
    

    def __init__(self, name: int32, codedData: CodedData[str]):
        self.name = name
        self.codedData = codedData

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.__dict__ == other.__dict__

    def __hash__(self):
        return hash(tuple(self.__dict__.values()))

    def __repr__(self):
        return str(tuple(self.__dict__.values()))


class EncapsulatedData3(metaclass=ConverterMeta):
    

    def __init__(self, name: int32, codedData: CodedData[Person]):
        self.name = name
        self.codedData = codedData

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.__dict__ == other.__dict__

    def __hash__(self):
        return hash(tuple(self.__dict__.values()))

    def __repr__(self):
        return str(tuple(self.__dict__.values()))


class EncapsulatedData4(metaclass=ConverterMeta):
    

    def __init__(self, name: int32, codedData: CodedData[List[str]]):
        self.name = name
        self.codedData = codedData

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.__dict__ == other.__dict__

    def __hash__(self):
        return hash(tuple(self.__dict__.values()))

    def __repr__(self):
        return str(tuple(self.__dict__.values()))
