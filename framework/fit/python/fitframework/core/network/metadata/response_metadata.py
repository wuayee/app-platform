# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
"""
功 能：响应的元数据定义
"""
from typing import Dict

from fitframework.api.exception import FitBaseException
from fitframework.const import DEFAULT_CODECS
from fitframework.core.exception.fit_exception import FitException, InternalErrorCode
from fitframework.core.network.metadata.metadata_utils import IntEncoder, IntDecoder, BitStream, TlvData


CURRENT_VERSION = 2
RESP_CODE_SUCCESS = 0
RESP_MSG_SUCCESS = 'success'


class ResponseMetadata:
    def __init__(self, data_format: int, version: int, degradable: bool,
                 code: int, msg: str, tlv_data: Dict[int, bytes]):
        """
        Args:
            version: ResponseMetadata版本号，默认为1
            data_format: 数据格式（JSON或ProtoBuf）
            degradable: 表示调用结果报错的情形下，该错误是否允许降级
            code: Fit调用的结果码，无错误时为默认的0
            msg: Fit调用的错误信息，无错误时为默认的“success”
            tlv_data: 扩展字段，Tlv格式
        """
        self.data_format = data_format
        self.version = version
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
        return ResponseMetadata(data_format, CURRENT_VERSION, False, RESP_CODE_SUCCESS, RESP_MSG_SUCCESS, tlv_data)

    @classmethod
    def failure(cls, data_format: int, err: FitBaseException, tlv_data: Dict[int, bytes] = None):
        tlv_data = {} if tlv_data is None else tlv_data
        return ResponseMetadata(data_format, CURRENT_VERSION, True, err.error_code, err.message, tlv_data)

    @classmethod
    def error_message(cls, data_format: int, code: int, message: str):
        return ResponseMetadata(data_format, CURRENT_VERSION, True, code, message, {})

    @classmethod
    def deserialize(cls, data: bytes):
        version = IntDecoder.from_bytes(data[:2], 'unsigned')
        data_format = IntDecoder.from_bytes(data[2: 3], 'unsigned')
        degradable = bool(IntDecoder.from_bytes(data[3: 4], 'unsigned'))
        code = IntDecoder.from_bytes(data[4: 8], 'unsigned')
        msg_len = IntDecoder.from_bytes(data[8: 12], 'unsigned')
        msg = data[12: 12+msg_len].decode(encoding=DEFAULT_CODECS)
        tlv_data = TlvData.deserialize(data[12 + msg_len:])
        return ResponseMetadata(data_format, version, degradable, code, msg, tlv_data)

    def serialize(self):
        # 这些也统一成tlv scheme？全部一个方式来处理？
        try:
            return IntEncoder.to_bytes(self.version, 2, 'unsigned') + \
                IntEncoder.to_bytes(self.data_format, 1, 'unsigned') + \
                IntEncoder.to_bytes(int(self.degradable), 1, "unsigned") + \
                IntEncoder.to_bytes(self.code, 4, 'unsigned') + \
                IntEncoder.to_bytes(len(self.msg), 4, 'unsigned') + \
                bytes(self.msg, encoding=DEFAULT_CODECS) + \
                TlvData.serialize(self.tlv_data)
        except ValueError:
            raise FitException(InternalErrorCode.INVALID_ARGUMENTS, f'invalid metadata found: {self}') from None


if __name__ == '__main__':
    resp_metadata_bytes = b'\x00\x02\x00\x00\x7f\x00\x00\x02\x00\x00\x00\n2130706434'
    resp_metadata = ResponseMetadata.deserialize(resp_metadata_bytes)
