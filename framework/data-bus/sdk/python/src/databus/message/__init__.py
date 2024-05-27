# coding: utf-8
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
from typing import Union
from . import (
    ApplyMemoryMessage,
    ApplyMemoryMessageResponse,
    ApplyPermissionMessage,
    ApplyPermissionMessageResponse,
    ErrorMessageResponse,
    MessageHeader,
    ReleaseMemoryMessage,
    ReleasePermissionMessage
)

from .ErrorType import ErrorType as CoreErrorType
from .MessageType import MessageType as CoreMessageType
from .PermissionType import PermissionType as CorePermissionType

CoreMessageResponseTypeHint = Union[
    ApplyMemoryMessageResponse.ApplyMemoryMessageResponse,
    ApplyPermissionMessageResponse.ApplyPermissionMessageResponse,
    ErrorMessageResponse.ErrorMessageResponse
]

# 默认给flatbuffers message的Builder预留的空间
DEFAULT_FLATBUFFERS_BUILDER_SIZE = 128
