# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
"""
Description:
记录日志
Create: 2024/7/8 9:26
"""
import time
from functools import wraps
from typing import List

from fitframework.api.logging import plugin_logger as logger
from common.model import Content, FlowData


def spend_time(func):
    """统计方法耗时"""
    @wraps(func)
    def wrap_func(cls, content: Content):
        start = time.time()
        content = func(cls, content)
        logger.info("fileName: %s, method: %s costs %.6f s", content.meta.get("fileName", ""),
                    func.__qualname__.split(".")[0], time.time() - start)
        return content
    return wrap_func


def entry_and_exit_log(func):
    """进入和函数运行完的日志"""
    @wraps(func)
    def wrap_func(flow_data_list: List[FlowData]):
        file_name = flow_data_list[0].passData.meta.get("fileName", "")
        logger.info("fileName: %s, method: %s start!", file_name, func.__name__)
        flow_data_list = func(flow_data_list)
        logger.info("fileName: %s, method: %s end!", file_name, func.__name__)
        return flow_data_list
    return wrap_func
