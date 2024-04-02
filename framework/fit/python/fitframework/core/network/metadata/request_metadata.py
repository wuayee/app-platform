# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：Fit通讯包头RequestMetadata结构体和解析
RequestMetadata:
version             short
data_format         byte
generic_version.major     byte
generic_version.minor     byte
generic_version.revision  byte
generic_id          string(hex)
fit_id length       short
fit_id              string
tags:
   tag.key          int
   tag.value length int
   tag.value        bytes （目前该value仅支持字符串数据的表达）
"""

from fitframework.const import DEFAULT_CODECS

from fitframework.core.network.metadata.metadata_utils import IntEncoder, IntDecoder, TlvData

CURRENT_VERSION = 2

# `METADATA_TAG`和`DATA_TAG`用于统一client和server两端的http json body的key使其一致
HTTP_METADATA_TAG = 'fit-metadata'
HTTP_DATA_TAG = 'data'

# "-bin" suffix is required to deliver binary data in grpc header value.
# see: https://github.com/grpc/grpc/blob/master/doc/PROTOCOL-HTTP2.md
GRPC_METADATA_TAG = 'fit-metadata-bin'


class GenericVersion(object):
    def __init__(self, major: int, minor: int, revision: int):
        self.major = major
        self.minor = minor
        self.revision = revision

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.__dict__ == other.__dict__

    def __repr__(self):
        return str(tuple(self.__dict__.values()))

    @classmethod
    def from_string(cls, value: str):
        split_value = value.split(".")
        return GenericVersion(int(split_value[0]), int(split_value[1]), int(split_value[2]))

    def serialize(self) -> bytes:
        return bytes([self.major, self.minor, self.revision])


class RequestMetadata(object):
    def __init__(self, version: int, data_format: int, generic_version: GenericVersion,
                 generic_id: str, fitable_id: str, tlv_data=None):
        if tlv_data is None:
            tlv_data = {}
        self.version = version
        self.data_format = data_format
        self.generic_version = generic_version
        self.generic_id = generic_id
        self.fitable_id = fitable_id
        self.tlv_data = tlv_data

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.__dict__ == other.__dict__

    def __repr__(self):
        return str(tuple(self.__dict__.values()))

    @classmethod
    def default(cls, generic_id: str, fitable_id: str, data_format: int):
        return RequestMetadata(CURRENT_VERSION, data_format, GenericVersion(1, 0, 0),
                               generic_id, fitable_id, {})

    @classmethod
    def deserialize(cls, data: bytes):
        version = IntDecoder.from_bytes(data[:2], 'unsigned')
        data_format = IntDecoder.from_bytes(data[2:3], 'unsigned')
        generic_version = GenericVersion(*tuple(data[3:6]))
        generic_id = data[6:22].hex()
        fitable_id_len = IntDecoder.from_bytes(data[22:24], 'unsigned')
        fitable_id = data[24:24 + fitable_id_len].decode(DEFAULT_CODECS)
        tlv_data = TlvData.deserialize(data[24 + fitable_id_len:])
        return RequestMetadata(version, data_format, generic_version, generic_id, fitable_id, tlv_data)

    def serialize(self) -> bytes:
        return IntEncoder.to_bytes(self.version, 2, 'unsigned') \
            + IntEncoder.to_bytes(self.data_format, 1, 'unsigned') \
            + self.generic_version.serialize() \
            + self.generic_id.encode(encoding="utf-8") \
            + IntEncoder.to_bytes(len(self.fitable_id), 2, 'unsigned') \
            + bytes(self.fitable_id, DEFAULT_CODECS) \
            + TlvData.serialize(self.tlv_data)

    def upset_a_tag(self, key: str, value: bytes):
        self.tlv_data[key] = value
