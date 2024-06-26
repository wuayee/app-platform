# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. -. All rights reserved.
import functools
import json
from typing import List, Any, Optional, Callable, Union

from langchain_core.runnables import RunnableConfig
from langchain_core.tools import BaseTool

from fitframework.core.repo.fitable_register import register_fitable


def register_function_tools(tools: List[BaseTool],
                            config: Optional[RunnableConfig] = None,
                            **kwargs: Any):
    """
    langchain 函数工具注册方法，注册无需 api key 的工具。

    Args:
        tools (List[BaseTool]): 表示 langchain 工具列表。
        config (Optional[RunnableConfig]): 表示 langchain runnable 配置信息。
        **kwargs (Any): 表示额外参数。
    """
    for tool in tools:
        register_api_tools(tool, [], tool.name, config, **kwargs)


def _pop_api_keys(input_args: dict, api_keys_name: List[str]) -> dict:
    if all(key in input_args.keys() for key in api_keys_name):
        api_keys_values = [input_args.pop(key) for key in api_keys_name]
        return dict(zip(api_keys_name, api_keys_values))
    else:
        raise ValueError(f"{input_args} not contain all api keys in {api_keys_name}")


def _invoke(input_args: dict, tool_builder: Union[Callable[[dict], BaseTool], BaseTool],
            api_keys_name: List[str],
            config: Optional[RunnableConfig] = None,
            **kwargs: Any) -> str:
    api_keys = _pop_api_keys(input_args, api_keys_name)
    if "__arg1" in input_args:
        _input_args = input_args["__arg1"]
    else:
        _input_args = input_args

    if isinstance(tool_builder, BaseTool):
        tool = tool_builder
    else:
        tool = tool_builder(api_keys)
    tool_ans = tool.invoke(_input_args, config, **kwargs)

    if not isinstance(tool_ans, str):
        try:
            content = json.dumps(tool_ans, ensure_ascii=False)
        except Exception:
            content = str(tool_ans)
    else:
        content = tool_ans
    return content


def register_api_tools(tool_builder: Union[Callable[[dict], BaseTool], BaseTool],
                       api_keys_name: List[str],
                       tool_name: str,
                       config: Optional[RunnableConfig] = None,
                       **kwargs: Any):
    """
    langchain api 工具注册方法。

    Args:
        tool_builder (Callable[[dict], BaseTool]): 表示 api 工具构造器。
        api_keys_name (List[str]): 表示工具的 api key 名称数组。
        tool_name (str): 工具名称。
        config (Optional[RunnableConfig]): 表示 langchain runnable 配置信息。
        **kwargs (Any): 表示额外参数。
    """
    tool_invoke = functools.partial(_invoke, tool_builder=tool_builder, api_keys_name=api_keys_name, config=config,
                                    **kwargs)
    tool_invoke.__module__ = register_api_tools.__module__
    tool_invoke.__annotations__ = {
        'input_args': dict,
        'return': str
    }
    register_fitable('langchain.tool', tool_name, False, [], tool_invoke)
