# -*- coding: utf-8 -*-
# !/usr/bin/python
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
import logging

logger = logging.getLogger(__name__)


def engine_select():
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
            engine_type = "GPU"

    try:
        import torch_npu
    except Exception as e:
        logger.info("Not NPU or torch_npu not installed")
    else:
        engine_type = "NPU"

    return engine_type
