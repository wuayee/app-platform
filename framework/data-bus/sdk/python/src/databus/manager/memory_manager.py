# coding: utf-8
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
""" `databus.manager.MemoryManager`类型, 管理从DataBus申请的内存块.

本文件内容为DataBus Python SDK的`databus.manager.MemoryManager`类实现.
`MemoryManager`类管理从DataBus申请的内存块,例如user_key <-> memory_id二者间的转换.
"""
from typing import Dict, Tuple, Optional


class MemoryManager:
    def __init__(self):
        self._memory_registry: Dict[str, Tuple[int, int]] = dict()

    def add_memory_block(self, user_key: str, memory_id: int, memory_size: int):
        self._memory_registry[user_key] = (memory_id, memory_size)

    def del_memory_block(self, user_key: str):
        if user_key in self._memory_registry:
            del self._memory_registry[user_key]

    def get_memory_info(self, user_key: str) -> Tuple[Optional[int], Optional[int]]:
        return self._memory_registry.get(user_key, (None, None))

    def to_memory_id(self, user_key: str) -> Optional[int]:
        return self._memory_registry.get(user_key, [None])[0]

    def reset(self):
        self._memory_registry.clear()
