# -*- coding: utf-8 -*-
# Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
"""
since: 2023/11/02 22:00
"""

import base64
from enum import Enum
from typing import Dict, List

from dataclasses import dataclass


@dataclass
class Content:
    """待处理的Content对象"""

    def __init__(self, data, text, meta):
        self.data: bytes = data if type(data) is bytes else base64.b64decode(data)
        self.text: str = text  # 字符串直接传输
        self.meta: Dict[str, str] = meta  # 待处理数据携带的元数据信息

    def to_dict(self):
        return vars(self)

    # a function that output this class to a string format in dev view
    def __repr__(self):
        return str(vars(self))


@dataclass
class FlowData:
    """http接口传入数据映射的python对象"""
    businessData: Dict[str, object]
    passData: Content
    contextData: Dict[str, str]

    def __init__(self, businessData=None, passData=None, contextData=None):
        if businessData is None:
            businessData = {}
        if passData is None:
            passData = {}
        if contextData is None:
            contextData = {}

        self.businessData: Dict[str, object] = businessData
        self.passData: Content = passData
        self.contextData: Dict[str, str] = contextData


class CodeEnum(str, Enum):
    """返回状态码枚举"""
    SUCCESS = 200
    ERROR = 500


@dataclass
class OutputModel:
    flowDataList: List[FlowData]
    code: CodeEnum
    msg: str

    def to_dict(self):
        for flowData in self.flowDataList:
            flowData.passData.data = base64.b64encode(flowData.passData.data.encode()).decode()
        return {
            "flowDataList": self.flowDataList,
            "code": self.code,
            "msg": self.msg
        }
