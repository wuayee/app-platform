# coding: utf-8
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
import logging
from contextlib import contextmanager


@contextmanager
def disable_logger():
    logger = logging.getLogger()
    old_config = logger.disabled
    logger.disabled = True
    try:
        yield None
    finally:
        logger.disabled = old_config
