# -*- coding: utf-8 -*-
# Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
"""
since: 2023/11/02 22:00
"""

import logging as logger
from typing import List

from common.model import FlowData
from .plugin import FileWithHighRepeatWordRateFilterPlugin


def remove_file_with_high_repeat_word_rate(flow_data_list: List[FlowData]):
    params = flow_data_list[0].businessData.get("params", {})
    logger.info("file_with_high_repeat_word_rate_filter_plugin get params, repeat_word_ratio is %s",
                params.get("repeatWordRatio"))
    contents = [flow_data.passData for flow_data in flow_data_list]
    contents = FileWithHighRepeatWordRateFilterPlugin(params).execute(contents)
    return [FlowData(businessData=flow_data.businessData, passData=content, contextData=flow_data.contextData)
            for flow_data, content in zip(flow_data_list, contents)]
