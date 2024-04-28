# coding: utf-8
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
""" `databus.client.Client`类型, 连接DataBus的默认类

本文件内容为DataBus Python SDK的`databus.client.Client`类实现.
`Client`类提供连接到DataBus内核的各种功能.

创建日期: 2024-04-23
"""
import databus.memory as memory


class Client:
    def __init__(self):
        pass

    def open(self):
        pass

    def close(self):
        pass

    def is_connected(self):
        pass

    def shared_malloc(self):
        pass

    def read_once(self, memory_id: int, size: int, offset: int = 0) -> bytes:
        self._check_read_parameter(memory_id, size, offset)
        return memory.read(memory_id, size, offset=offset)

    def write_once(self):
        pass

    def _check_read_parameter(self, memory_id: int, size: int, offset: int):
        if not self.is_connected():
            raise SystemError
        if memory_id < 0:
            raise ValueError(f"invalid memory_id {memory_id}")
        if size <= 0:
            raise ValueError(f"invalid read size {size}")
        if offset < 0:
            raise ValueError(f"invalid offset {offset}")
