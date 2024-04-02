# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
"""
功 能：获取server的地址
"""
from fit_common_struct.registry_v1 import AddressForRegistryV1

from fitframework import const
from fitframework.api.decorators import value, fit


@fit(const.SERVICE_DB_GET_REGISTER_SERVER_GEN_ID)
def get_server_address_by_server_id(server_id: int) -> AddressForRegistryV1:
    """
    根据server_id获得本地server地址, server_id通过插件配置获得

    Args:
        server_id: 支持的协议id，默认3（指grpc协议）
    """
    pass


@value('registry-center.client.service_id', 3)
def _service_id():
    pass


def get_address() -> AddressForRegistryV1:
    return get_server_address_by_server_id(_service_id())
