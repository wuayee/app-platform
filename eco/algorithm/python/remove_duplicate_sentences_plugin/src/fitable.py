# -*- coding: utf-8 -*-
# Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
"""
since: 2023/11/02 22:00
"""
from typing import List

from common.model import FlowData
from eco.algorithm.python.remove_duplicate_sentences_plugin.src.plugin import DuplicateSentencesFilterPlugin


def remove_duplicate_sentences(flow_data_list: List[FlowData]):
    contents = [flow_data.passData for flow_data in flow_data_list]
    contents = DuplicateSentencesFilterPlugin.execute(contents)
    return [FlowData(businessData=flow_data.businessData, passData=content)
            for flow_data, content in zip(flow_data_list, contents)]
