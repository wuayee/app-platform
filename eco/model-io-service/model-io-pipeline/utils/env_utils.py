# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
import logging

logger = logging.getLogger(__name__)

GPU = 'GPU'
NPU = 'NPU'


class GpuEnvTools:
    @classmethod
    def select_device(cls, force: bool = False):
        '''
        GPU选择最优运算资源设备
        :return: int 设备号
        '''
        import torch
        device = 0
        max_mem = 0
        for i in range(torch.cuda.device_count()):
            npu_stat = torch.cuda.mem_get_info(i)[0]
            if npu_stat > max_mem:
                max_mem = npu_stat
                device = i
        return "cuda:{}".format(device)


class NpuEnvTools:
    @classmethod
    def select_device(cls, force: bool = False):
        '''
        NPU选择最优运算资源设备
        :return: int 设备号
        '''
        import torch_npu
        device = 0
        max_mem = 0
        if not force:
            for i in range(torch_npu.npu.device_count()):
                npu_stat = torch_npu.npu.mem_get_info(i)[0]
                if npu_stat > max_mem:
                    max_mem = npu_stat
                    device = i
        return "npu:{}".format(device)


class EnvTools:
    def __init__(self):
        self.env_tool = None
        device = self.engine_select()
        if device == NPU:
            self.env_tool = NpuEnvTools()
        else:
            self.env_tool = GpuEnvTools()

    @classmethod
    def engine_select(cls):
        """
        判断是否有可用显卡以及显卡种类
        :return: 显卡种类 None/NPU/GPU/
        """
        engine_type = None
        try:
            import torch
        except Exception as e:
            logger.info("Not GPU or torch not installed")
        else:
            if torch.cuda.is_available():
                engine_type = GPU

        try:
            import torch_npu
        except Exception as e:
            logger.info("Not NPU or torch_npu not installed")
        else:
            engine_type = NPU

        return engine_type

    def select_device(self, force: bool = False):
        '''
        选择最优运算资源设备
        :return: int 设备号
        '''
        return self.env_tool.select_device(force)


ENVTOOLS = EnvTools()
