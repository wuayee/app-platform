# -*- coding: utf-8 -*-
# Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
"""
since: 2023/11/07 16:25
"""
from typing import List

from common.model import FlowData
from eco.algorithm.python.content_cleaner_plugin.src.plugin import ContentCleanerPlugin


def clean_content(flow_data_list: List[FlowData]):
    contents = [flow_data.passData for flow_data in flow_data_list]
    contents = ContentCleanerPlugin().execute(contents)
    return [FlowData(businessData=flow_data.businessData, passData=content)
            for flow_data, content in zip(flow_data_list, contents)]
