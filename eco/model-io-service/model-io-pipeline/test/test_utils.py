# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
from unittest.mock import MagicMock
import unittest
import sys
import set_path
from utils.env_utils import EnvTools, GpuEnvTools, NpuEnvTools, ENVTOOLS
from utils.param_utils import clean_args


def mock_gpu_env():
    torch_module_str = 'torch'
    torch_cuda_module_str = 'torch.cuda'
    torch_npu_module_str = 'torch_npu'
    sys.modules[torch_module_str] = MagicMock()
    sys.modules[torch_cuda_module_str] = MagicMock()
    sys.modules[torch_npu_module_str] = None


def mock_npu_env():
    torch_module_str = 'torch'
    torch_cuda_module_str = 'torch.cuda'
    torch_npu_module_str = 'torch_npu'
    sys.modules[torch_module_str] = None
    sys.modules[torch_cuda_module_str] = None
    sys.modules[torch_npu_module_str] = MagicMock()


def mock_normal_env():
    torch_module_str = 'torch'
    torch_cuda_module_str = 'torch.cuda'
    torch_npu_module_str = 'torch_npu'
    sys.modules[torch_module_str] = None
    sys.modules[torch_cuda_module_str] = None
    sys.modules[torch_npu_module_str] = None


class TestEnvUtils(unittest.TestCase):

    def test_env_tools_init(self):
        mock_gpu_env()
        tools = EnvTools()
        self.assertIsInstance(tools.env_tool, GpuEnvTools)

        mock_npu_env()
        tools = EnvTools()
        self.assertIsInstance(tools.env_tool, NpuEnvTools)

        mock_normal_env()
        tools = EnvTools()
        self.assertIsInstance(tools.env_tool, GpuEnvTools)

    def test_gpu_select_device(self):
        mock_gpu_env()
        import torch
        torch.cuda.device_count.return_value = 3

        def mem_info_side_effect(index):
            mock_mem_info = {0: (5, 10), 1: (10, 10), 2: (6, 10)}
            return mock_mem_info.get(index)

        torch.cuda.mem_get_info.side_effect = mem_info_side_effect
        self.assertEqual(EnvTools().select_device(), "cuda:1")

    def test_npu_select_device(self):
        mock_npu_env()
        import torch_npu
        torch_npu.npu.device_count.return_value = 3

        def mem_info_side_effect(index):
            mock_mem_info = {0: (5, 10), 1: (10, 10), 2: (6, 10)}
            return mock_mem_info.get(index)

        torch_npu.npu.mem_get_info.side_effect = mem_info_side_effect
        self.assertEqual(EnvTools().select_device(), "npu:1")


class TestParamUtils(unittest.TestCase):
    def test_clean_args(self):
        example_args = {
            'name': 'Alice',
            'age': None,
            'hobbies': [],
            'email': '',
            'address': 'Wonderland'
        }
        cleaned_args = {
            'name': 'Alice',
            'address': 'Wonderland'
        }
        result = clean_args(example_args)
        self.assertEqual(result, cleaned_args)
