# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：底座环境上下文。包括输入参数和环境变量Map
"""
from collections import ChainMap

_runtime_context = ChainMap()


def add_context(context: dict):
    global _runtime_context
    _runtime_context = _runtime_context.new_child(context)


def get_item(item):
    return _runtime_context.get(item)
