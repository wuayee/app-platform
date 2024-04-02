# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
"""
功 能：提供获取注册中心地址的工具module
"""

from fit_common_struct.registry_v1 import AddressForRegistryV1

from fitframework.api.decorators import value
from fitframework.utils import tools

PUSH_MODE = 'push'
PULL_MODE = 'pull'


@value('registry-center.client.mode')
def registry_client_mode():
    pass


@value('registry-pull-frequency', default_value=300)
def registry_pull_frequency():
    pass


@value('registry-center.client.invalid_address_ttl', 60, converter=int)
def invalid_address_ttl():
    pass


@value("registry-center.client.registry_fitable_frequency", default_value=300, converter=int)
def registry_fitable_frequency():
    pass


@value('registry-center.server.addresses', converter=tools.to_list)
def _registry_server_addresses() -> list:
    pass


@value('registry-center.server.protocol', converter=int)
def _registry_server_protocol():
    pass


@value('registry-center.server.formats', tools.to_list)
def _registry_server_formats() -> list:
    pass


@value('registry-center.service_ids', tools.to_list)
def _registry_server_generic_ids() -> list:
    pass


@value('worker-environment.env')
def _worker_env():
    pass


SECONDS_OF_ONE_DAY = 86400


@value("registry-center.client.expired_invalid_address_clear_frequency",
       default_value=SECONDS_OF_ONE_DAY, converter=int)
def clear_expired_invalid_address_frequency():
    pass


def get_registry_addresses_from_configuration():
    return [_build_address_from_dict(addr) for addr in _registry_server_addresses()]


def _build_address_from_dict(addr: str) -> AddressForRegistryV1:
    # 注册中心的地址配置默认从本地读取，故其对应的环境标默认与当前引擎worker的环境标保持一致
    ip, port = addr.split(':')
    return AddressForRegistryV1(host=ip, port=int(port),
                                protocol=_registry_server_protocol(), environment=_worker_env(),
                                formats=_registry_server_formats(), id='')
