# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：eventing单元测试
"""
import unittest

from fitframework.api.decorators import register_event
from fitframework.testing.test_support import FitTestSupport
from fitframework.utils.eventing import notify, _registered_events
from fitframework.api.enums import FrameworkEvent


class MyTestCase(FitTestSupport):

    def setUp(self) -> None:
        _registered_events.clear()

    def test_registry_without_filter_function(self):
        arg = [1, 2, 3]

        @register_event(FrameworkEvent.APPLICATION_STARTED)
        def fire(args):
            self.assertEqual(arg, args)

        notify(FrameworkEvent.APPLICATION_STARTED, arg)

    def test_registry_with_filter_function(self):
        arg = [1, 2, 3, 4]

        @register_event(FrameworkEvent.APPLICATION_STARTED, _filter=lambda x: x % 2 == 0)
        def fire(args):
            for i in args:
                assert i % 2 == 0

        notify(FrameworkEvent.APPLICATION_STARTED, arg)


if __name__ == '__main__':
    unittest.main()
