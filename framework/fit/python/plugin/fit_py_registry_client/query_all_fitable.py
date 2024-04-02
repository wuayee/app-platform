# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
"""
功 能：提供获取所有@fit的服务地址,在pull模式下定时向注册中心请求所有的依赖
"""
from typing import List
from fitframework.api.logging import sys_plugin_logger
from fitframework import const
from fitframework.api.decorators import fit, scheduled_executor, value

from .entity import ServiceAddress, FitableInfo, FitableInstance, convert_fitable_inst_to_service_address, \
    convert_old_fitable_to_new_fitable
from .registry_client import get_fitable_address_from_registry_server_and_update_cache
from .registry_repo import fitable_address_cache, get_all_fitable_in_cache


@fit(const.QUERY_FIT_SERVICE_V2_GEN_ID)
def query_fitable_addresses_v2(fitables: List[FitableInfo], worker_id: str) -> List[FitableInstance]:
    pass


@fit(const.RUNTIME_GET_WORKER_ID_GEN_ID)
def get_runtime_worker_id() -> str:
    pass


def query_all_at_fit() -> List[ServiceAddress]:
    """
    向配置中心查询所有的@fit依赖
    Returns:
        查询到的所有@fit依赖

    """
    all_fitables = [convert_old_fitable_to_new_fitable(each) for each in fitable_address_cache.keys()]
    if not all_fitables:
        return []
    instances = query_fitable_addresses_v2(all_fitables, get_runtime_worker_id())
    return [convert_fitable_inst_to_service_address(_) for _ in filter(lambda _: _.applicationInstances, instances)]


@value('registry-center.client.pull-frequency', default_value=60)
def pull_frequency():
    pass


@value('registry-center.client.mode')
def _registry_client_mode():
    pass


@scheduled_executor(pull_frequency() if _registry_client_mode() == 'pull' else 0)
def flush_registry_address():
    sys_plugin_logger.debug('flush fitable address')
    fitables = get_all_fitable_in_cache()
    for fitable in fitables:
        get_fitable_address_from_registry_server_and_update_cache(fitable)
