# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
"""
功 能：响应的元数据定义
"""
from typing import Dict

from fitframework.api.exception import FitBaseException

CURRENT_VERSION = 2
RESP_CODE_SUCCESS = 0
RESP_MSG_SUCCESS = 'success'


class ResponseMetadata:
    def __init__(self, data_format: int, degradable: bool,
                 code: int, msg: str, tlv_data: Dict[int, bytes]):
        """
        Args:
            data_format: 数据格式（JSON或ProtoBuf）
            degradable: 表示调用结果报错的情形下，该错误是否允许降级
            code: Fit调用的结果码，无错误时为默认的0
            msg: Fit调用的错误信息，无错误时为默认的“success”
            tlv_data: 扩展字段，Tlv格式
        """
        self.data_format = data_format
        self.degradable = degradable
        self.code = code
        self.msg = msg
        self.tlv_data = tlv_data

    def __repr__(self):
        return str(tuple(self.__dict__.values()))

    @property
    def is_err_degradable(self):
        return self.degradable

    @classmethod
    def success(cls, data_format: int, tlv_data: Dict[int, bytes] = None):
        tlv_data = {} if tlv_data is None else tlv_data
        return ResponseMetadata(data_format, False, RESP_CODE_SUCCESS, RESP_MSG_SUCCESS, tlv_data)

    @classmethod
    def failure(cls, data_format: int, err: FitBaseException, tlv_data: Dict[int, bytes] = None):
        tlv_data = {} if tlv_data is None else tlv_data
        return ResponseMetadata(data_format, True, err.error_code, err.message, tlv_data)

    @classmethod
    def error_message(cls, data_format: int, code: int, message: str):
        return ResponseMetadata(data_format, True, code, message, {})
