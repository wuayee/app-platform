# coding: utf-8
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.

import unittest
from unittest.mock import MagicMock, Mock, patch

from test.mock import memory_io_mock, memory_manager_mock
from test.utils import disable_logger
from databus.dto import ReadRequest, WriteRequest
from databus.exceptions import CoreError
from databus.message import DataBusErrorCode, CorePermissionType

with (patch("databus.memory_io.read", new=memory_io_mock.read),
      patch("databus.memory_io.write", new=memory_io_mock.write),
      patch("databus.manager.MemoryManager", new=memory_manager_mock)):
    from src.databus.client.sdk_client import SdkClientImpl


class TestSdkClientImpl(unittest.TestCase):
    def setUp(self):
        self._socket_mock = Mock()
        self._mem_io_mock = memory_io_mock
        self._mem_io_mock.reset_mock()
        self._mem_manager_mock = memory_manager_mock()
        self._mem_manager_mock.reset_mock()
        self._client = SdkClientImpl()
        self._client.open_with_socket(self._socket_mock)

    def tearDown(self):
        self._client = None
        self._socket_mock = None

    def test_is_connected(self):
        self.assertTrue(self._client.is_connected())
        # 测试没有连接的情况
        self._client.open_with_socket(None)
        self.assertFalse(self._client.is_connected())

    def test_shared_malloc(self):
        user_key, memory_id, size = "key", 10, 100
        response_mock = Mock()
        self._socket_mock.send_shared_malloc_message.return_value = response_mock
        response_mock.ErrorType.return_value = DataBusErrorCode.None_
        response_mock.MemoryKey.return_value = memory_id
        response_mock.MemorySize.return_value = size

        self._client.shared_malloc(user_key, size)
        self._socket_mock.send_shared_malloc_message.assert_called_once_with(user_key, size)

    def test_shared_malloc_with_invalid_param(self):
        user_key, memory_id, size = "key", 10, 100
        self.assertRaises(ValueError, lambda: self._client.shared_malloc("", size))
        self.assertRaises(ValueError, lambda: self._client.shared_malloc(user_key, -1))

    def test_shared_malloc_with_core_error(self):
        user_key, memory_id, size = "key", 10, 100
        response_mock = Mock()
        self._socket_mock.send_shared_malloc_message.return_value = response_mock
        response_mock.ErrorType.return_value = DataBusErrorCode.MallocFailed
        response_mock.MemoryKey.return_value = None
        response_mock.MemorySize.return_value = None

        with disable_logger():
            self.assertRaises(CoreError, lambda: self._client.shared_malloc(user_key, size))
        self._socket_mock.send_shared_malloc_message.assert_called_once_with(user_key, size)

    def test_shared_free(self):
        user_key, memory_id, size = "some key", 6, 30
        self._mem_manager_mock.to_memory_id.return_value = memory_id

        self._client.shared_free(user_key)
        self._socket_mock.send_shared_free_message.assert_called_once_with(user_key=user_key, memory_id=memory_id)
        self._mem_manager_mock.to_memory_id.assert_called_once_with(user_key)
        self._mem_manager_mock.del_memory_block.assert_called_once_with(user_key)

    def test_read_once_with_reading_user_data(self):
        user_key, memory_id, size, offset, is_operating_user_data = "user_key", 1, 1, 0, True
        expected_contents, expect_user_data = b"hello", b"\x02"
        self._socket_mock.with_permission = MagicMock()
        apply_permission_response_mock = Mock()
        self._socket_mock.with_permission().__enter__.return_value = apply_permission_response_mock
        apply_permission_response_mock.MemoryKey.return_value = memory_id
        apply_permission_response_mock.MemorySize.return_value = size
        apply_permission_response_mock.UserDataLength.return_value = len(expect_user_data)
        apply_permission_response_mock.UserData.return_value = int(expect_user_data.hex())
        self._mem_manager_mock.to_memory_id.return_value = memory_id
        self._mem_io_mock.read.return_value = expected_contents

        request = ReadRequest(user_key, size, offset=offset, is_operating_user_data=is_operating_user_data)
        response = self._client.read_once(request)
        self.assertEqual(expected_contents, response.contents)
        self.assertEqual(expect_user_data, response.user_data)

        self._mem_io_mock.read.assert_called_once_with(memory_id, size, offset=offset)
        self._socket_mock.with_permission.assert_called_with(CorePermissionType.Read, request=request,
                                                             memory_id=memory_id)

    def test_write_once(self):
        contents, user_data = b"world", b"\x01"
        user_key, memory_id, size, offset = "user_key", 1, len(contents) + 1, 0
        self._socket_mock.with_permission = MagicMock()
        apply_permission_response_mock = Mock()
        self._socket_mock.with_permission().__enter__.return_value = apply_permission_response_mock
        apply_permission_response_mock.MemorySize.return_value = size
        apply_permission_response_mock.MemoryKey.return_value = memory_id
        self._mem_manager_mock.get_memory_info.return_value = memory_id, size
        self._mem_io_mock.write.return_value = size

        request = WriteRequest(user_key, contents, offset=offset, is_operating_user_data=True, user_data=user_data)
        self.assertTrue(self._client.write_once(request))
        self._mem_io_mock.write.assert_called_once_with(memory_id, contents, offset=offset)

    def test_write_once_with_invalid_access(self):
        contents = b"python"
        user_key, memory_id = "user_key", 1
        self._socket_mock.with_permission = MagicMock()
        apply_permission_response_mock = Mock()
        self._socket_mock.with_permission().__enter__.return_value = apply_permission_response_mock
        apply_permission_response_mock.MemorySize.return_value = len(contents) - 1
        apply_permission_response_mock.MemoryKey.return_value = memory_id
        self._mem_io_mock.write = Mock()

        with disable_logger():
            request = WriteRequest(user_key, contents, is_operating_user_data=False)
            self.assertEqual(0, self._client.write_once(request))

        self._mem_io_mock.write.assert_not_called()
