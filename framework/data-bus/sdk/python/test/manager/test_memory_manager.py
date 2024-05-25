# coding: utf-8
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
import unittest
from src.databus.manager import MemoryManager


class TestMemoryManager(unittest.TestCase):
    _mem_manger = None

    def setUp(self):
        self._mem_manger = MemoryManager()

    def tearDown(self):
        self._mem_manger.reset()

    def test_get_memory_info_with_existing_key(self):
        key, memory_id, size = "user_key", 1, 1
        self._mem_manger.add_memory_block(key, memory_id, size)
        actual_memory_id, actual_size = self._mem_manger.get_memory_info(key)
        self.assertEqual(actual_memory_id, memory_id)
        self.assertEqual(actual_size, size)

    def test_get_memory_info_with_non_existing_key(self):
        key = "user_key"
        actual_memory_id, actual_size = self._mem_manger.get_memory_info(key)
        self.assertIsNone(actual_memory_id)
        self.assertIsNone(actual_size)

    def test_to_memory_id_with_existing_key(self):
        key, memory_id, size = "user_key", 10, 1
        self._mem_manger.add_memory_block(key, memory_id, size)
        self.assertEqual(self._mem_manger.to_memory_id(key), memory_id)

    def test_to_memory_id_with_non_existing_key(self):
        key = "user_key"
        self.assertIsNone(self._mem_manger.to_memory_id(key))

    def test_add_del_memory_block(self):
        key, memory_id, size = "user_key", 1, 1
        self._mem_manger.add_memory_block(key, memory_id, size)
        self.assertEqual(self._mem_manger.to_memory_id(key), memory_id)
        self._mem_manger.del_memory_block(key)
        self.assertIsNone(self._mem_manger.to_memory_id(key))
