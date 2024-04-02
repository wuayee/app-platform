# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
"""
功 能：提供获取所有@fit的服务地址,在pull模式下定时向注册中心请求所有的依赖
"""
from fitframework.api.logging import sys_plugin_logger
from fitframework.api.decorators import scheduled_executor, value
from .registry_client import get_fitable_address_from_registry_server_and_update_cache
from .registry_repo import get_all_fitable_in_cache


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
