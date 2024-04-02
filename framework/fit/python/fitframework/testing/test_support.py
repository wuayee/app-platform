# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：Fit插件单元测试用基类。插件的单元测试注意事项：
测试的Module需要在测试方法中实时import，不能在Module头import
"""

import unittest
from unittest.mock import Mock
from fitframework.api import decorators

from fitframework.utils import eventing


class FitTestSupport(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.cached_fit_ref = decorators.fit
        cls.cached_fitable_ref = decorators.fitable
        cls.cached_value_ref = decorators.value
        cls.cached_private_fitable = decorators.private_fitable
        cls.cached_private_fit = decorators.private_fit
        cls.cached_register_event = eventing.register

        decorators.fit = Mock(return_value=lambda func: func)
        decorators.fitable = Mock(return_value=lambda func: func)
        decorators.value = Mock(return_value=lambda func: func)
        decorators.private_fitable = Mock(side_effect=lambda func: func)
        decorators.private_fit = Mock(side_effect=lambda func: func)
        eventing.register_event = Mock(return_value=lambda func: func)

    @classmethod
    def tearDownClass(cls):
        decorators.fit = cls.cached_fit_ref
        decorators.fitable = cls.cached_fitable_ref
        decorators.value = cls.cached_value_ref
        decorators.private_fitable = cls.cached_private_fitable
        decorators.private_fit = cls.cached_private_fit
        eventing.register = cls.cached_register_event
