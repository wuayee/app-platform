# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
"""
功 能：数据实体定义
"""
from typing import List


class RequestContext:
    def __init__(self, timeout: int):
        self.timeout = timeout


class Pair:
    def __init__(self, key: str, value: str):
        self.key = key
        self.value = value


class GlobalContext:
    def __init__(self, pair: List[Pair]):
        self.pair = pair

