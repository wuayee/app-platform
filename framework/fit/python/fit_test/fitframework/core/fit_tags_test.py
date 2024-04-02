# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：fit_tags单元测试
"""
import unittest
from fitframework.core.broker.broker_utils import GenericableTagEnum


class GenericableTagEnumTestCase(unittest.TestCase):
    def test_is_local_only(self):
        self.assertTrue(GenericableTagEnum.is_local_only('localOnly'))

    def test_is_invoke_all(self):
        self.assertTrue(GenericableTagEnum.is_invoke_all('invokeAll'))


if __name__ == '__main__':
    unittest.main()
