# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：ProtobufConverter, _NullArguments单元测试
"""
import unittest
from fitframework.core.network import protobuf_serializer


class ProtobufConverterTestCase(unittest.TestCase):
    def test_null_arguments(self):
        null_args = protobuf_serializer._NullArguments(size=10)
        null_args.mark_null(2)
        null_args.mark_null(8)
        self.assertFalse(null_args.is_null(1))
        self.assertTrue(null_args.is_null(2))
        self.assertFalse(null_args.is_null(3))
        self.assertTrue(null_args.is_null(8))
        self.assertFalse(null_args.is_null(9))
        self.assertEqual(b'\x04\x01', null_args.as_bytes())


if __name__ == '__main__':
    unittest.main()
