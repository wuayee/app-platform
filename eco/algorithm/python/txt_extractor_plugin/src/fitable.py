# -*- coding: utf-8 -*-
# Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
"""
since: 2023/11/02 22:00
"""

from typing import List

from common.model import FlowData
from .plugin import TxtExtractorPlugin


def extract_txt(flow_data_list: List[FlowData]):
    contents = [flow_data.passData for flow_data in flow_data_list]
    contents = TxtExtractorPlugin.execute(contents)
    return [FlowData(businessData=flow_data.businessData, passData=content, contextData=flow_data.contextData)
            for flow_data, content in zip(flow_data_list, contents)]
