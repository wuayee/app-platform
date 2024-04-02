# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
"""
功 能：提供缓存访问接口的系统插件。
"""
from fitframework.api.logging import sys_plugin_logger
from fitframework.core.exception.fit_exception import CapacityOverflowException
from fitframework.api.decorators import fitable, local_context
from fitframework.utils.tools import get_memory_usage
from .cache import Cache, CacheValueMetadata

data_cache = Cache()


@fitable("com.huawei.fit.bigdata.cache.create.str", "local-worker")
def create_str_in_cache(content: str) -> str:
    return data_cache.create(content)


@fitable("com.huawei.fit.bigdata.cache.create.bytes", "local-worker")
def create_bytes_in_cache(content: bytes) -> str:
    return data_cache.create(content)


@fitable("com.huawei.fit.bigdata.cache.read.meta", "local-worker")
def read_meta_from_cache(index: str) -> CacheValueMetadata:
    value = data_cache.read(index)
    if value is None:
        return CacheValueMetadata(None, None)
    return CacheValueMetadata("str" if isinstance(value, str) else "bytes", len(value))


@fitable("com.huawei.fit.bigdata.cache.read.str", "local-worker")
def read_str_from_cache(index: str) -> str:
    return data_cache.read(index)


@fitable("com.huawei.fit.bigdata.cache.read.bytes", "local-worker")
def read_bytes_from_cache(index: str) -> bytes:
    return data_cache.read(index)


@fitable("com.huawei.fit.bigdata.cache.delete", "local-worker")
def delete_value_from_cache(index: str) -> None:
    return data_cache.delete(index)


@fitable("com.huawei.fit.bigdata.cache.get.info", "local-worker")
def get_info_from_cache() -> (int, int):
    return data_cache.get_cache_info()


@local_context('memory.max-size', converter=int, default_value=1099511627776)
def _memory_max_size():
    pass


@fitable("com.huawei.fit.bigdata.cache.validate.capacity", "local-worker")
def validate_capacity_from_cache(to_allocate_memory: int) -> None:
    memory_usage = get_memory_usage()
    if to_allocate_memory + memory_usage > _memory_max_size():
        sys_plugin_logger.warning(f"validate not passed. "
                                  f"[configuredMaxMem={'{:,}'.format(_memory_max_size())}, "
                                  f"usedMem={'{:,}'.format(memory_usage)}, "
                                  f"freeMem={'{:,}'.format(_memory_max_size() - memory_usage)}, "
                                  f"toAllocatedMem={'{:,}'.format(to_allocate_memory)}]")
        raise CapacityOverflowException(
            f"No enough remaining space for data caching. [configuredMaxMem={_memory_max_size()}, "
            f"usedMem={memory_usage}, freeMem={_memory_max_size() - memory_usage}, "
            f"toAllocatedMem={to_allocate_memory}].", _memory_max_size(), memory_usage, to_allocate_memory)
