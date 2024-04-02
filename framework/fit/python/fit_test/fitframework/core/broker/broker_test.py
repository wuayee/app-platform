# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：BrokerTemplate单元测试
"""
import unittest
from functools import wraps
from unittest.mock import patch

from fitframework.core.broker.broker import BrokerTemplate
from fitframework.core.broker.broker_utils import FitableIdentifier, IdType

_TEST_IDENTIFIER = FitableIdentifier('fitable1', IdType.id)


def _mock_normal_process(function):
    @wraps(function)
    def wrapped(*args):
        with patch.object(BrokerTemplate, 'execute', return_value='ok'):
            with patch.object(BrokerTemplate, 'load_balancing', return_value='address'):
                with patch.object(BrokerTemplate, 'routing', return_value='fitable1'):
                    return function(*args)

    return wrapped


class BrokerTemplateTest(unittest.TestCase):
    """broker.py Unit Test"""

    @_mock_normal_process
    def test_invoke_happy_path_with_fitable(self):
        result = BrokerTemplate().fit_ffp_invoke("gen1", _TEST_IDENTIFIER, None, None)
        self.assertEqual('ok', result)

    @_mock_normal_process
    def test_invoke_when_before_raise_exception(self):
        with patch.object(BrokerTemplate, 'on_before', side_effect=Exception()):
            self.assertEqual('ok',
                             BrokerTemplate().fit_ffp_invoke("gen1", _TEST_IDENTIFIER, None, None))

    @_mock_normal_process
    def test_invoke_when_after_raise_exception(self):
        with patch.object(BrokerTemplate, 'on_after', side_effect=Exception()):
            self.assertEqual('ok',
                             BrokerTemplate().fit_ffp_invoke('gen1', _TEST_IDENTIFIER, None, None))

    @_mock_normal_process
    def test_invoke_when_validate_raise_exception(self):
        with self.assertRaises(Exception):
            with patch.object(BrokerTemplate, 'on_validate', side_effect=Exception()):
                BrokerTemplate().fit_ffp_invoke('gen1', _TEST_IDENTIFIER, None, None)

    @_mock_normal_process
    def test_invoke_when_route_return_none(self):
        with self.assertRaises(Exception):
            with patch.object(BrokerTemplate, 'route', return_value=None):
                BrokerTemplate().fit_ffp_invoke('gen1', _TEST_IDENTIFIER, None, None)

    @_mock_normal_process
    def test_invoke_when_load_balancing_return_none(self):
        with self.assertRaises(Exception):
            with patch.object(BrokerTemplate, 'load_balancing', return_value=None):
                BrokerTemplate().fit_ffp_invoke('gen1', _TEST_IDENTIFIER, None, None)

    @_mock_normal_process
    def test_invoke_when_trigger_degradation(self):
        def execute_side_effect(address, generic_id, run_id, *args):
            if run_id == 'fitable1':
                raise Exception()
            return 'ok'

        with patch.object(BrokerTemplate, 'execute', side_effect=execute_side_effect):
            with patch.object(BrokerTemplate, 'get_degradation', return_value='degradation_id'):
                self.assertEqual('ok',
                                 BrokerTemplate().fit_ffp_invoke('gen1', _TEST_IDENTIFIER, None, None))


if __name__ == '__main__':
    unittest.main()
