# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：调用上下文
"""
import threading
from typing import Any

_context = threading.local()

ROUTING_PROTOCOL = 'routing.protocol'

TRACING_STACK_PROP = 'trace.stack'
GLOBAL_TRACING_ID_PROP = 'trace.id'
GLOBAL_PORT_PROP = 'trace.server_port'


def get_context_value(key: str):
    return getattr(_context, key, None)


def put_context_value(key: str, value: Any):
    setattr(_context, key, value)


def get_and_del_context_value(key: str):
    val = get_context_value(key)
    if val is not None:
        del_context_value(key)
    return val


def del_context_value(key: str):
    delattr(_context, key)
