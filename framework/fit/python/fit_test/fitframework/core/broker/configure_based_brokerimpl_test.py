# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：configure_based_brokerimpl单元测试
"""
import unittest
from unittest.mock import patch

import fitframework.core.network.metadata.metadata_utils

from com_huawei_fitLab_core_component_endpoint.entity import Endpoint
from com_huawei_fitLab_core_component_address.entity import Address as newAddress
from com_huawei_fit_registry_registrycommon.entity import Address

from fitframework.core.broker import configure_based_brokerimpl, select_broker, trace
from fitframework.core.broker import remote_invoker
from fitframework.core.broker.broker import BrokerTemplate
from fitframework.core.broker.broker_utils import FitableIdentifier, IdType
from fitframework.core.broker.configure_based_brokerimpl import ConfigureBasedBroker
from fitframework.core.repo import service_repo
from fitframework.testing.test_support import FitTestSupport

_MOCK_FITABLE_ID = '925d87507d00400294aeca71b9f19eh8'

patch_trace = patch('fitframework.core.broker.trace.print_trace', lambda x: x)
patch_trace.start()


def mock_function(in_param: str) -> str:
    pass


class ConfigureBasedBrokerTest(FitTestSupport):
    class MockBroker(BrokerTemplate):
        def fit_ffp_invoke(self, generic_id: str, fitable_identifier: str, *args, route_filter=None,
                           address_filter=None):
            return f"{generic_id}{fitable_identifier}{len(args)}"

    @patch.object(BrokerTemplate, 'invoke')
    def test_invoke(self, mock_invoke):
        args = 'gen1', FitableIdentifier('a1', IdType.alias), None, None, 'abc'
        ConfigureBasedBroker().fit_ffp_invoke(*args)
        mock_invoke.assert_called_once_with(*args)

    @patch.object(trace, '_ignore_trace')
    @patch.object(service_repo, 'get_fit_or_fitable_ref', return_value='fun')
    @patch.object(configure_based_brokerimpl, 'get_fit_ffp_fitable_id', return_value='f1')
    @patch.object(BrokerTemplate, 'invoke')
    def test_on_validate(self, mock_super_invoke, _, *__):
        ConfigureBasedBroker().on_validate('gen1', None, 'abc')
        mock_super_invoke.assert_called_once_with('gen1', FitableIdentifier('f1', IdType.id),
                                                  'fun',
                                                  'abc', route_filter=None, address_filter=None)

    @patch.object(trace, '_ignore_trace')
    @patch.object(select_broker.BrokerBuilder, '_get_fit_ref')
    @patch.object(configure_based_brokerimpl, 'get_fit_ffp_fitable_id', return_value='f1')
    @patch.object(BrokerTemplate, 'invoke')
    def test_on_before(self, mock_super_invoke, _, mock_fun, __):
        mock_fun.return_value = 'fun'
        ConfigureBasedBroker().on_before('gen1', None, 'abc')
        mock_super_invoke.assert_called_once_with('gen1', FitableIdentifier('f1', IdType.id),
                                                  'fun',
                                                  'abc', route_filter=None, address_filter=None)

    @patch.object(trace, '_ignore_trace')
    @patch.object(select_broker.BrokerBuilder, '_get_fit_ref', return_value='fun')
    @patch.object(configure_based_brokerimpl, 'get_fit_ffp_fitable_id', return_value='f1')
    @patch.object(BrokerTemplate, 'invoke')
    def test_on_after(self, mock_super_invoke, _, *__):
        ConfigureBasedBroker().on_after('gen1', None, 'abc')
        mock_super_invoke.assert_called_once_with('gen1', FitableIdentifier('f1', IdType.id), 'fun',
                                                  'abc', route_filter=None, address_filter=None)

    @patch.object(trace, '_ignore_trace')
    @patch.object(select_broker.BrokerBuilder, '_get_fit_ref', return_value='fun')
    @patch.object(configure_based_brokerimpl, 'get_fit_ffp_fitable_id', return_value='f1')
    @patch.object(BrokerTemplate, 'invoke')
    def test_on_error(self, mock_super_invoke, _, *__):
        ConfigureBasedBroker().on_error('gen1', None, 'abc')
        mock_super_invoke.assert_called_once_with('gen1', FitableIdentifier('f1', IdType.id), 'fun',
                                                  'abc', route_filter=None, address_filter=None)

    @patch.object(trace, '_ignore_trace')
    @patch.object(select_broker.BrokerBuilder, '_get_fit_ref', return_value='fun')
    @patch.object(configure_based_brokerimpl, '_routing_by_rule', return_value=None)
    @patch.object(configure_based_brokerimpl, 'get_genericable_tags', return_value=[])
    @patch.object(BrokerTemplate, 'invoke', return_value='f1')
    @patch.object(configure_based_brokerimpl, 'get_fit_ffp_fitable_id')
    def test_routing_default(self, mock_get_fit_ffp_fitable_id, mock_invoke, _, *__):
        ffp_dict = {'route.dynamic': 'rid'}
        mock_get_fit_ffp_fitable_id.side_effect = lambda _, key: ffp_dict.get(key)
        self.assertListEqual(['f1'], ConfigureBasedBroker().routing('gen1', None, None, 'abc',
                                                                    route_filter=None))
        mock_invoke.assert_called_once_with('gen1', FitableIdentifier('rid', IdType.id), 'fun',
                                            'abc', route_filter=None, address_filter=None)

    @patch.object(configure_based_brokerimpl, 'get_all_fitable_ids')
    def test_routing_with_filter(self, mock_get_all_fit_fitable_ids):
        mock_get_all_fit_fitable_ids.return_value = [('%s' % _MOCK_FITABLE_ID)]
        route_filter1 = lambda t: t == _MOCK_FITABLE_ID
        self.assertListEqual([_MOCK_FITABLE_ID],
                             ConfigureBasedBroker().routing(_MOCK_FITABLE_ID,
                                                            None, None, None,
                                                            route_filter=route_filter1))

    @patch.object(configure_based_brokerimpl, 'get_genericable_tags', return_value=['invokeAll'])
    @patch.object(configure_based_brokerimpl, 'get_all_fitable_ids',
                  return_value=['fitable1', 'fitable2'])
    def test_routing_invoke_all(self, *_):
        self.assertListEqual(['fitable1', 'fitable2'],
                             ConfigureBasedBroker().routing('gen1', None, None, 'abc'))

    @patch.object(configure_based_brokerimpl, '_is_invoke_all', return_value=False)
    @patch.object(configure_based_brokerimpl, 'environment', return_value='alpha')
    @patch.object(configure_based_brokerimpl, 'get_genericable_rule', return_value={
        configure_based_brokerimpl._FIT_META_GEN_RULE_ID_KEY: 'test_rule',
        configure_based_brokerimpl._FIT_META_GEN_RULE_TYPE_KEY: 'T, P'})
    @patch.object(configure_based_brokerimpl, 'get_genericable_params', return_value={
        'a': {'taggers': None}, 'b': {'taggers': {'big_id': {}, 'small_id': {}}},
        'c': {'taggers': {}}, 'd': {}})
    @patch.object(configure_based_brokerimpl, 'routing_rule_execute',
                  return_value='test_fitable_id')
    @patch.object(configure_based_brokerimpl, 'resolve_tags', return_value=['filtered_big'])
    def test_routing_by_rule(self, mock_resolve_tags, mock_routing_rule_execute,
                             mock_get_genericable_params, mock_genericable_rule, mock_environment,
                             _):
        def test_genericable(a: int, b: str, c: list, d: tuple, e: dict):
            pass

        test_gen_id = '123'

        fitable_id_s = ConfigureBasedBroker().routing(test_gen_id, None,
                                                      test_genericable,
                                                      *(1, '2', [3], (4,), {5: '5'}))
        self.assertListEqual(fitable_id_s, ['test_fitable_id'])
        mock_environment.assert_called_once()
        mock_genericable_rule.assert_called_once_with(test_gen_id)
        mock_get_genericable_params.assert_called_once_with(test_gen_id)
        mock_routing_rule_execute.assert_called_once_with('alpha', 'test_rule',
                                                          '{"P": {"arg0": 1, "arg1": "2", '
                                                          '"arg2": [3], "arg3": [4], '
                                                          '"arg4": {"5": "5"}}, '
                                                          '"T": {"arg1": ["filtered_big"]}}')
        mock_resolve_tags.assert_called_once_with('alpha', ['big_id', 'small_id'],
                                                  '{"P": {"arg1": "2"}}')

    @patch.object(configure_based_brokerimpl, 'get_genericable_tags', return_value=[])
    @patch.object(service_repo, 'get_fitable_ref')
    def test_load_balancing_local_first(self, mock_get_fitable_ref, _):
        func = lambda in_param: in_param + ' -> out'
        mock_get_fitable_ref.return_value = func
        result = ConfigureBasedBroker().load_balancing('gen1', 'fitable1')
        self.assertEqual(func, result)

    @patch.object(configure_based_brokerimpl, '_get_fit_service_address_with_priorities')
    @patch.object(configure_based_brokerimpl, '_load_balance_env_filtering')
    def test_load_balancing_filter(self, mock_load_balance_env_filtering, _):
        address = Address('127.0.0.1', 8001, 'asd', 3, 0, 0)
        endpoint = [Endpoint(address, 3, 0, 0, 100)]

        mock_load_balance_env_filtering.return_value = endpoint
        address_filter1 = lambda endpoint: endpoint.address.port == 8001

        result = ConfigureBasedBroker().load_balancing('gen1', 'fitable1',
                                                       address_filter=address_filter1)
        self.assertEqual(8001, result.address.port)

    @patch.object(configure_based_brokerimpl, 'get_genericable_tags', return_value=['localOnly'])
    @patch.object(service_repo, 'get_fitable_ref', return_value=None)
    def test_load_balancing_force_local(self, *_):
        result = ConfigureBasedBroker().load_balancing('gen1', 'fitable1')
        self.assertIsNone(result)

    @patch.object(configure_based_brokerimpl, 'get_genericable_tags', return_value=[])
    @patch.object(configure_based_brokerimpl, 'get_fit_service_address_list', return_value=[])
    @patch.object(service_repo, 'get_fitable_ref', return_value=None)
    def test_load_balancing_remote_no_available(self, *_):
        result = ConfigureBasedBroker().load_balancing('gen1', 'fitable1')
        self.assertIsNone(result)

    @patch.object(trace, '_ignore_trace')
    @patch.object(configure_based_brokerimpl, 'get_genericable_tags', return_value=[])
    @patch.object(service_repo, 'get_fitable_ref', return_value=None)
    @patch.object(configure_based_brokerimpl, 'get_fit_ffp_fitable_id', return_value=None)
    @patch.object(configure_based_brokerimpl, 'get_registered_clients', return_value=[2, 3])
    @patch.object(remote_invoker, 'get_supported_formats', return_value=[0, 1])
    @patch.object(remote_invoker, 'protocol_priors', return_value=remote_invoker.default_priority)
    @patch.object(BrokerTemplate, 'invoke')
    @patch.object(configure_based_brokerimpl, '_load_balance_env_filtering')
    @patch.object(configure_based_brokerimpl, '_load_balancing')
    @patch.object(configure_based_brokerimpl, 'get_fit_service_address_list')
    def test_load_balancing_remote(self, mock_get_address_list, mock_lb, mock_filtering,
                                   *_):
        address1 = Address("host2", 200, "host2:200", 3, [0], 'unittest')
        address2 = Address("host1", 100, "host1:100", 2, [1], 'unittest')
        address3 = Address("host2", 200, "host2:200", 2, [0], 'unittest')

        address4 = Endpoint(('host2:200', 'host2', 200), 3, [0], 'unittest', 100)
        address5 = Endpoint(('host1:100', 'host1', 100), 2, [1], 'unittest', 100)
        address6 = Endpoint(('host2:200', 'host2', 200), 2, [0], 'unittest', 100)
        mock_get_address_list.return_value = [address1, address2, address3]
        mock_filtering.side_effect = lambda _, addresses: addresses
        mock_lb.side_effect = lambda _, addresses: addresses
        results = ConfigureBasedBroker().load_balancing('gen1', 'fitable1', address_filter=None)
        self.assertEqual(2, len(results))
        self.assertEqual(address5.address[0],
                         results[0].address.workerId)

        self.assertEqual(address4.address[0], results[1].address.workerId)

    def test_execute_local(self):
        result = ConfigureBasedBroker().fit_execute(lambda in_param: in_param + ' -> out', 'gen1',
                                                    'fitable1', None, 'in', None)
        self.assertEqual('in -> out', result)

    @patch.object(remote_invoker, 'request_response', return_value=b'\x12\x02ok\x1a\x05\n\x03out')
    @patch.object(remote_invoker, 'get_supported_formats', return_value=[0, 1])
    @patch.object(remote_invoker, 'protocol_priors', return_value=remote_invoker.default_priority)
    @patch.object(fitframework.core.network.metadata.metadata.metadata_utils.TagLengthValuesUtil, 'generate_tags')
    @patch.object(configure_based_brokerimpl, 'print_trace')
    def test_execute_remote(self, *_):
        address = newAddress("workId", "host1", "8080")
        remote_address = Endpoint(address, 0, [3], "unittest", 100)
        result = ConfigureBasedBroker().fit_execute(remote_address, '1aa1', 'fitable1', mock_function,
                                                    'in', None)
        self.assertEqual('out', result)


if __name__ == '__main__':
    unittest.main()

patch_trace.stop()
