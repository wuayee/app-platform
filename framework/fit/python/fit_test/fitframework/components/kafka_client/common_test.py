# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：common单元测试
"""
import unittest

from fit_py_message_queue_agent import common


class CommonTest(unittest.TestCase):
    def test_encode_decode_in_whitelist(self):
        encoded = common.encode('Topic1')
        self.assertEqual('Topic1', encoded)
        decoded = common.decode(encoded)
        self.assertEqual('Topic1', decoded)

    def test_encode_decode_out_of_whitelist(self):
        encoded = common.encode('_Topic1/123._-')
        self.assertEqual('__Topic1_2f123_2e___2d', encoded)
        decoded = common.decode(encoded)
        self.assertEqual('_Topic1/123._-', decoded)


if __name__ == '__main__':
    unittest.main()
