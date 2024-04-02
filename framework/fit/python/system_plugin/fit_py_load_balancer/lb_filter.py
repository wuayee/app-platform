# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：load balance环境标filter服务
"""
from itertools import groupby
from typing import List

from fitframework import const
from fitframework.api.decorators import fitable, local_context
from fitframework.api.logging import sys_plugin_logger
from fit_common_struct.core import Endpoint


@local_context(key='worker-environment.env-seq')
def env_seq() -> str:
    """ a,b,c... """
    pass


@local_context(key="worker-environment.env")
def worker_env() -> str:
    """环境标"""
    pass


def is_endpoint_valid(endpoint: Endpoint):
    return endpoint.weight == 100


def filter_by_weight(endpoints: List[Endpoint]):
    valid_endpoint_s = [endpoint for endpoint in endpoints if is_endpoint_valid(endpoint)]
    if len(valid_endpoint_s) == 0:
        # 如果地址都失效了，那么为了不影响调用，仍旧把失效的地址返回
        valid_endpoint_s = endpoints
    return valid_endpoint_s


@fitable(const.LOAD_BALANCING_FILTER_GEN_ID, const.LOAD_BALANCING_FILTER_FIT_ID)
def do_filter(endpoints: List[Endpoint]) -> List[Endpoint]:
    endpoints = filter_by_weight(endpoints)
    if not env_seq():
        return endpoints

    env_seq_ = env_seq().split(',')
    if worker_env() not in env_seq_:
        return endpoints

    worker_env_ = worker_env()

    endpoints.sort(key=lambda _address: _address.environment)
    env_addresses = {env: list(val) for env, val in groupby(endpoints, lambda address: address.environment)}
    for env in env_seq_[env_seq_.index(worker_env_):]:
        if env in env_addresses:
            return env_addresses[env]
    sys_plugin_logger.warning("no env match for %s ,the endpoints is %s,the env_seq is %s " % (
        worker_env_, endpoints, env_seq_))
    return None
