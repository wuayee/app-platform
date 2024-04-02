# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：metadata单元测试
"""
import copy
import unittest
from fitframework.core.network.metadata import MetaData, GenericVersion


_METADATA_OBJECT = MetaData(version=10, data_format=1,
                            generic_version=GenericVersion(major=1, minor=1, revision=0),
                            generic_id='00112233445566778899aabbccddeeff',
                            fitable_id='ohmygod')

_METADATA_DATA = bytes.fromhex('000a' + '01' + '010100' + '00112233445566778899aabbccddeeff' + '0007') + b'ohmygod'


class MetadataTest(unittest.TestCase):
    def setUp(self) -> None:
        self.test_object = copy.deepcopy(_METADATA_OBJECT)
        self.test_data = _METADATA_DATA

    def test_serialize_no_tags(self):
        self._test_serialize()

    def test_deserialize_no_tags(self):
        self._test_deserialize()

    def test_serialize_with_tags(self):
        self._attach_tags()
        self._test_serialize()

    def test_deserialize_with_tags(self):
        self._attach_tags()
        self._test_deserialize()

    def _test_serialize(self):
        byte_data = self.test_object.serialize()
        print(self.test_data)
        print(byte_data)
        self.assertEqual(self.test_data, byte_data)

    def _test_deserialize(self):
        meta_object = MetaData.deserialize(self.test_data)
        self.assertEqual(self.test_object, meta_object)

    def _attach_tags(self):
        self.test_object.upset_a_tag(0x20, b'tag1')
        self.test_object.upset_a_tag(0x30, b'tag2')
        self.test_data += b'\x00\x00\x00 ' + b'\x00\x00\x00\x04' + b'tag1' + \
                          b'\x00\x00\x000' + b'\x00\x00\x00\x04' + b'tag2'


if __name__ == '__main__':
    unittest.main()
