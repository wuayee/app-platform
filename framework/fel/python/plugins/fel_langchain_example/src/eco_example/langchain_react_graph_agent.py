# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. -. All rights reserved.

import os
from typing import List, Optional, Any, Dict, Sequence, Union, Type, Callable

from langchain_core.callbacks import CallbackManagerForLLMRun
from langchain_core.language_models import BaseChatModel, LanguageModelInput
from langchain_core.messages import BaseMessage
from langchain_core.outputs import ChatResult, ChatGeneration
from langchain_core.runnables import Runnable
from langchain_core.tools import tool, BaseTool
from langchain_core.utils.function_calling import convert_to_openai_tool
from langchain_openai import ChatOpenAI
from langgraph.prebuilt import create_react_agent
from openai import BaseModel

os.environ["no_proxy"] = "*"
os.environ["OPENAI_API_KEY"] = "EMPTY"


@tool
def check_weather_stub(location: str) -> str:
    """Return the weather forecast for the specified location."""
    return f"It's sunny in {location}"


class ToolCallingModel(BaseChatModel):
    model_raw: BaseChatModel
    model_with_tools: Runnable = None

    @property
    def _llm_type(self) -> str:
        return "tool-calling-model"

    def bind_tools(
            self,
            tools: Sequence[Union[Dict[str, Any], Type[BaseModel], Callable, BaseTool]],
            **kwargs: Any,
    ) -> Runnable[LanguageModelInput, BaseMessage]:
        if len(tools) > 0:
            tool_msg = [convert_to_openai_tool(t) for t in tools]
            self.model_with_tools = self.model_raw.bind(tools=tool_msg, tool_choice="auto")
        return self

    def _generate(
            self,
            messages: List[BaseMessage],
            stop: Optional[List[str]] = None,
            run_manager: Optional[CallbackManagerForLLMRun] = None,
            **kwargs: Any,
    ) -> ChatResult:
        model_input = "\n".join([m.content for m in messages])
        model = self.model_with_tools if self.model_with_tools is not None else self.model_raw
        message_out = model.invoke(model_input)
        return ChatResult(generations=[ChatGeneration(message=message_out)])


tools_stub = [check_weather_stub]
model_qwen14b = ChatOpenAI(openai_api_key="EMPTY", openai_api_base='http://51.36.139.114:8011/v1')
graph = create_react_agent(ToolCallingModel(model_raw=model_qwen14b), tools=tools_stub)
