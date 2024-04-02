# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
"""
功 能：提供registry client的repo
"""
from typing import List, Dict

from fit_common_struct.registry_v1 import FitableForRegistryV1, AddressForRegistryV1

fitable_address_cache: Dict[FitableForRegistryV1, List[AddressForRegistryV1]] = {}
# service meta 的缓存
service_meta_cache = {}


def get_addresses_from_cache(fitable: FitableForRegistryV1):
    return fitable_address_cache.get(fitable)


def update_addresses_in_cache(fitable: FitableForRegistryV1, addresses: List[AddressForRegistryV1]):
    fitable_address_cache[fitable] = addresses


def get_all_fitable_in_cache() -> List[FitableForRegistryV1]:
    return fitable_address_cache.keys()
