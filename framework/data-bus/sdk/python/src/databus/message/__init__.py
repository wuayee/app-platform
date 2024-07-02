# coding: utf-8
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
from typing import Union
from . import (
    ApplyMemoryMessage,
    ApplyMemoryMessageResponse,
    ApplyPermissionMessage,
    ApplyPermissionMessageResponse,
    ErrorMessageResponse,
    GetMetaDataMessage,
    GetMetaDataMessageResponse,
    MessageHeader,
    ReleaseMemoryMessage,
    ReleasePermissionMessage
)

from .ErrorType import ErrorType as DataBusErrorCode
from .MessageType import MessageType as CoreMessageType
from .PermissionType import PermissionType as CorePermissionType

CoreMessageResponseTypeHint = Union[
    ApplyMemoryMessageResponse.ApplyMemoryMessageResponse,
    ApplyPermissionMessageResponse.ApplyPermissionMessageResponse,
    ErrorMessageResponse.ErrorMessageResponse
]

# flatbuffers message的最大长度
MAX_MESSAGE_LENGTH = 128
# flatbuffers message header的长度
MESSAGE_HEADER_LENGTH = 32
# 消息发送类型与返回类型对应关系
MESSAGE_RESPONSE_MAPPING = {
    CoreMessageType.ApplyMemory: ApplyMemoryMessageResponse.ApplyMemoryMessageResponse,
    CoreMessageType.ApplyPermission: ApplyPermissionMessageResponse.ApplyPermissionMessageResponse,
    CoreMessageType.Error: ErrorMessageResponse.ErrorMessageResponse,
    CoreMessageType.GetMetaData: GetMetaDataMessageResponse.GetMetaDataMessageResponse,
}
