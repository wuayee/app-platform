# -*- coding: utf-8 -*-
# !/usr/bin/python
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
import time
from typing import Dict, List, Literal, Optional, Union
from pydantic import BaseModel, Field
from llm_service.utils.request_utils import random_uuid


class BaseModelCompatibleDict(BaseModel):
    def __getitem__(self, item):
        return getattr(self, item)

    def __setitem__(self, key, value):
        setattr(self, key, value)

    def model_dump(self, **kwargs):
        return super().model_dump(exclude_none=True, **kwargs)

    def model_dump_json(self, **kwargs):
        return super().model_dump_json(**kwargs)

    def get(self, key, default=None):
        try:
            value = getattr(self, key)
            if value:
                return value
            else:
                return default
        except AttributeError:
            return default


class ErrorResponse(BaseModel):
    object: str = "error"
    message: str
    type: str
    param: Optional[str] = None
    code: int


class UsageInfo(BaseModel):
    prompt_tokens: int = 0
    total_tokens: int = 0
    completion_tokens: Optional[int] = 0


class FunctionDefinition(BaseModelCompatibleDict):
    name: str
    description: str
    parameters: Optional[Dict[str, object]] = None

    def __repr__(self):
        return f'FunctionDefinition({self.model_dump()})'


class FunctionCall(BaseModelCompatibleDict):
    name: str
    arguments: str

    def __init__(self, name: str, arguments: str):
        super().__init__(name=name, arguments=arguments)

    def __repr__(self):
        return f'FunctionCall({self.model_dump()})'


class ToolCall(BaseModelCompatibleDict):
    type: str
    function: FunctionDefinition


class ToolCallResponse(BaseModelCompatibleDict):
    id: str
    type: str
    function: FunctionCall


class ChatMessage(BaseModelCompatibleDict):
    role: str
    content: Optional[str] = None
    name: Optional[str] = None
    tools: Optional[List[ToolCall]] = None
    tool_choice: Optional[str] = None
    tool_calls: Optional[List[ToolCallResponse]] = None


class ChatCompletionRequest(BaseModelCompatibleDict):
    model: str
    messages: List[ChatMessage]
    tools: Optional[List[ToolCall]] = None
    tool_choice: Optional[str] = None
    temperature: Optional[float] = 0.7
    top_p: Optional[float] = 1.0
    n: Optional[int] = 1
    max_tokens: Optional[int] = 8192
    stop: Optional[Union[str, List[str]]] = Field(default_factory=list)
    stream: Optional[bool] = False
    presence_penalty: Optional[float] = 0.0
    frequency_penalty: Optional[float] = 0.0
    logit_bias: Optional[Dict[str, float]] = None
    user: Optional[str] = None
    # Additional parameters supported by vLLM
    best_of: Optional[int] = None
    top_k: Optional[int] = -1
    ignore_eos: Optional[bool] = False
    use_beam_search: Optional[bool] = False
    stop_token_ids: Optional[List[int]] = Field(default_factory=list)
    skip_special_tokens: Optional[bool] = True
    spaces_between_special_tokens: Optional[bool] = True
    add_generation_prompt: Optional[bool] = True
    echo: Optional[bool] = False
    repetition_penalty: Optional[float] = 1.0
    min_p: Optional[float] = 0.0
    include_stop_str_in_output: Optional[bool] = False
    length_penalty: Optional[float] = 1.0


class ChoiceDeltaToolCall(BaseModelCompatibleDict):
    index: int
    id: str
    type: str
    function: FunctionDefinition


class DeltaMessage(BaseModelCompatibleDict):
    role: Optional[str] = None
    content: Optional[str] = None
    tool_calls: Optional[List[ChoiceDeltaToolCall]] = None


class ChatCompletionResponseStreamChoice(BaseModelCompatibleDict):
    index: int
    delta: Union[DeltaMessage, ChatMessage]
    finish_reason: Optional[Literal["stop", "length"]] = None


class ChatCompletionStreamResponse(BaseModelCompatibleDict):
    id: str = Field(default_factory=lambda: f"{random_uuid()}")
    object: str = "chat.completion.chunk"
    created: int = Field(default_factory=lambda: int(time.time()))
    model: str
    choices: List[ChatCompletionResponseStreamChoice]
    usage: Optional[UsageInfo] = Field(default=None)


class ChatCompletionResponseChoice(BaseModelCompatibleDict):
    index: int
    message: Union[ChatMessage, ChatMessage]
    finish_reason: Optional[Literal["stop", "length", "tool_calls"]] = None


class ChatCompletionResponseChoice(BaseModelCompatibleDict):
    index: int
    message: Union[ChatMessage, ChatMessage]
    finish_reason: Optional[Literal["stop", "length", "tool_calls"]] = None


class ChatCompletionResponse(BaseModelCompatibleDict):
    id: str = Field(default_factory=lambda: f"{random_uuid()}")
    object: str = "chat.completion"
    created: int = Field(default_factory=lambda: int(time.time()))
    model: str
    choices: List[ChatCompletionResponseChoice]
    usage: UsageInfo
