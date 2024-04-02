# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：broker_utils.get_priority_index单元测试
"""
import unittest

from com_huawei_fit_registry_registrycommon.entity import Address
from fitframework.core.broker.broker_utils import get_priority_index


class MyTestCase(unittest.TestCase):
    def test_grpc_protobuf_priority(self):
        addresses = [
            Address(host='localhost', port=8889, protocol=3, formats=[0, 1], id='localhost:8889',
                    environment='test'),
            Address(host='localhost', port=8888, protocol=2, formats=[0, 1], id='localhost:8888',
                    environment='test')]
        priority = ['GRPC:PROTOBUF']
        addresses.sort(
            key=lambda _address: get_priority_index(_address.protocol, _address.formats, priority))
        assert addresses[0].protocol == 3

        addresses[0].formats.sort(
            key=lambda _format: get_priority_index(addresses[0].protocol, [_format], priority))
        assert addresses[0].formats[0] == 0

    def test_grpc_json_priority(self):
        addresses = [
            Address(host='localhost', port=8889, protocol=3, formats=[0, 1], id='localhost:8889',
                    environment='test'),
            Address(host='localhost', port=8888, protocol=2, formats=[0, 1], id='localhost:8888',
                    environment='test')]
        priority = ['GRPC:JSON']
        addresses.sort(
            key=lambda _address: get_priority_index(_address.protocol, _address.formats, priority))
        assert addresses[0].protocol == 3

        addresses[0].formats.sort(
            key=lambda _format: get_priority_index(addresses[0].protocol, [_format], priority))
        assert addresses[0].formats[0] == 1

    def test_http_json_priority(self):
        addresses = [
            Address(host='localhost', port=8889, protocol=3, formats=[0, 1], id='localhost:8889',
                    environment='test'),
            Address(host='localhost', port=8888, protocol=2, formats=[0, 1], id='localhost:8888',
                    environment='test')]
        priority = ['HTTP:JSON']
        addresses.sort(
            key=lambda _address: get_priority_index(_address.protocol, _address.formats, priority))
        assert addresses[0].protocol == 2

        addresses[0].formats.sort(
            key=lambda _format: get_priority_index(addresses[0].protocol, [_format], priority))
        assert addresses[0].formats[0] == 1

    def test_http_protobuf_priority(self):
        addresses = [
            Address(host='localhost', port=8889, protocol=3, formats=[0, 1], id='localhost:8889',
                    environment='test'),
            Address(host='localhost', port=8888, protocol=2, formats=[0, 1], id='localhost:8888',
                    environment='test')]
        priority = ['HTTP:PROTOBUF']
        addresses.sort(
            key=lambda _address: get_priority_index(_address.protocol, _address.formats, priority))
        assert addresses[0].protocol == 2

        addresses[0].formats.sort(
            key=lambda _format: get_priority_index(addresses[0].protocol, [_format], priority))
        assert addresses[0].formats[0] == 0


if __name__ == '__main__':
    unittest.main()
