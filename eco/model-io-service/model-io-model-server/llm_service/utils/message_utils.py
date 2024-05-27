# -*- coding: utf-8 -*-
# !/usr/bin/python
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
import re
import json
import copy
import uuid
from typing import Dict, List

from llm_service.utils.protocol import ChatMessage, ToolCallResponse, FunctionCall, ToolCall, FunctionDefinition
from llm_service.utils.request_utils import random_uuid


def has_chinese_chars(data) -> bool:
    """
    判断是否含有中文字符
    :param data: 输入信息
    :return: 是否包含中文字符
    """
    text = f'{data}'
    return len(re.findall(r'[\u4e00-\u9fff]+', text)) > 0


def get_function_description(function: FunctionDefinition) -> str:
    """
    按模板组装函数信息
    :param function: 传入函数信息
    :return: 组装好的函数信息字符串
    """
    tool_desc_template = {
        'zh':
            '### {name_for_human}\n\n{name_for_model}: {description_for_model} 输入参数：{parameters} {args_format}',
        'en':
            '### {name_for_human}\n\n{name_for_model}: {description_for_model} Parameters：{parameters} {args_format}'
    }
    if has_chinese_chars(function):
        tool_desc = tool_desc_template['zh']
    else:
        tool_desc = tool_desc_template['en']

    name = function.get('name', None)
    name_for_human = function.get('name_for_human', name)
    name_for_model = function.get('name_for_model', name)
    args_format = function.get('args_format', '')
    return tool_desc.format(name_for_human=name_for_human,
                            name_for_model=name_for_model,
                            description_for_model=function['description'],
                            parameters=json.dumps(function['parameters'],
                                                  ensure_ascii=False),
                            args_format=args_format).rstrip()


def prepend_fncall_system(fncall_model: object,
                          messages: List[ChatMessage],
                          functions: List[FunctionDefinition]) -> List[ChatMessage]:
    """
    按照模板拼装函数调用字符串，并插入到用户传入的messages中
    :param fncall_model: 函数调用工具类
    :param messages: 用户调用大模型的messages
    :param functions: 用户传入的函数列表
    :return: 拼装函数调用后的新messages
    """
    tool_desc_template = fncall_model.fn_call_template_en

    for message in messages[::-1]:
        if message.role in ('user',):
            if has_chinese_chars(message.content):
                tool_desc_template = fncall_model.fn_call_template_zh
            break
    tool_descs = '\n\n'.join(
        get_function_description(function) for function in functions)
    tool_names = ','.join(
        function.get('name', function.get('name_for_model', ''))
        for function in functions)
    tool_system = tool_desc_template.format(tool_descs=tool_descs,
                                            tool_names=tool_names)

    # 如果messages中没有system message，则加入
    if messages[0].role != 'system':
        messages.insert(0, ChatMessage(role="system", content="You are a helpful assitant"))

    messages[0].content += tool_system
    return messages


def prepend_tools_system(fncall_model: object,
                         messages: List[ChatMessage],
                         tools: List[ToolCall]) -> List[ChatMessage]:
    """
    按照模板拼装函数调用字符串，并插入到用户传入的messages中
    :param fncall_model: 函数调用工具类
    :param messages: 用户调用大模型的messages
    :param tools: 用户传入的工具字段，包含一个函数列表
    :return: 拼装函数调用后的新messages
    """
    functions = [tool.function for tool in tools]
    return prepend_fncall_system(fncall_model, messages, functions)


def preprocess_fncall_messages(fncall_model: object, messages: List[ChatMessage]) -> List[ChatMessage]:
    """
    使用特定函数调用工具类，组装传入的函数与message
    :param fncall_model: 特定函数调用工具类
    :param messages: 用户传入的message
    :return: 使用特定函数调用工具类组装后的message
    """

    new_messages = []
    message_role = 'assistant'
    for msg in copy.deepcopy(messages):
        role, content = msg.role, msg.content
        if role in ('system', 'user'):
            new_messages.append(msg)
        elif role == message_role:
            content = (content or "")
            fn_call = None
            if msg.tool_calls:
                fn_call = msg.tool_calls[0].function
            if fn_call:
                func_content = ''
                f_name = fn_call.name
                f_args = fn_call.arguments
                func_content += f'\n{fncall_model.fn_name}: {f_name}'
                func_content += f'\n{fncall_model.fn_args}: {f_args}'
                content += func_content
            if new_messages[-1].role == message_role:
                new_messages[-1].content += content
            else:
                new_messages.append(ChatMessage(role=role, content=content))
        elif role == "tool":
            if content:
                f_result = (content or "")
            new_messages[-1].content += f'\n{fncall_model.fn_result}: {f_result}\n{fncall_model.fn_exit}: '
        else:
            raise TypeError

    if new_messages[-1].role == message_role:
        last_msg = new_messages[-1].content
        if last_msg.endswith(f'{fncall_model.fn_exit}: '):
            new_messages[-1].content = last_msg[:-2]

    if new_messages and new_messages[-1].role == message_role:
        usr = new_messages[-2].content
        bot = new_messages[-1].content
        if isinstance(usr, str) and isinstance(bot, str):
            usr = usr + '\n\n' + bot
        elif isinstance(usr, list) and isinstance(bot, list):
            usr = usr + '\n\n' + bot
        else:
            raise NotImplementedError
        text_to_complete = copy.deepcopy(new_messages[-2])
        text_to_complete.content = usr
        new_messages = new_messages[:-2] + [text_to_complete]

    return new_messages


def postprocess_fncall_messages(fncall_model: object, msg: ChatMessage) -> ChatMessage:
    """
    使用特定函数调用工具类，提取函数调用部分，组装为新message
    :param fncall_model: 特定函数调用工具类
    :param msg: 模型返回的messages
    :return: 使用特定函数调用工具类组装后的message
    """
    if msg.role in ('system', 'user'):
        return msg

    role, item_text = msg.role, msg.content
    item_text.replace(': ', '')

    i = item_text.find(f'{fncall_model.fn_name}:')
    if i < 0:  # no function call
        msg.content = fncall_model.remove_special_tokens(item_text)
        return msg

    if i > 0:
        item_text = item_text[i:]

    for part in item_text.split(f'{fncall_model.fn_name}:'):
        if not part:
            continue

        part = part.rstrip('\n')  # 移除尾部换行符
        i = part.find(f'\n{fncall_model.fn_args}:')
        j = part.find(f'\n{fncall_model.fn_result}:')

        fn_name, fn_args = '', ''
        if i < 0:
            fn_name = part.strip()
        else:
            fn_name = part[:i].strip()

        if j < 0:
            fn_args = part[i + len(f'\n{fncall_model.fn_args}:'):].strip()
        else:
            fn_args = part[i + len(f'\n{fncall_model.fn_args}:'):j].strip()

    return ChatMessage(
        role='assistant',
        content="",
        tool_calls=[ToolCallResponse(
            id=random_uuid(),
            type='function',
            function=FunctionCall(
                name=fncall_model.remove_special_tokens(fn_name),
                arguments=fncall_model.remove_special_tokens(fn_args)
            )
        )],
    )
