# -*- coding: utf-8 -*-
# !/usr/bin/python
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
import sys
import unittest
from unittest.mock import MagicMock
import json

import set_path
from llm_service.utils.protocol import ChatMessage, ToolCall, ToolCallResponse, FunctionCall
from llm_service.utils.env_utils import engine_select
from llm_service.utils.message_utils import has_chinese_chars, get_function_description, prepend_fncall_system, \
    prepend_tools_system, postprocess_fncall_messages
from llm_service.engine.function_calling import QwenFnCallModel, DefaultFnCallModel

EMPTY_STR = ''
NAME = 'name'
DESCRIPTION = 'description'
PARAMETERS = 'parameters'
TYPE = 'type'
OBJECT_TYPE = 'object'
FUNCTION_TYPE = 'function'
STR_TYPE = 'string'
PROPERTIES = 'properties'
LOCATION_PROPERTIES = 'location'
UNIT_PROPERTIES = 'unit'
TITLE_PROPERTIES = 'title'
LOCATION_PROPERTIES_DESC = '城市名'
REQUIRED = 'required'
WEATHER_FN_NAME = "查询天气"
WEATHER_FN_DESC = "查询指定地点的天气"
WEATHER_FN_PARAM = {
    TYPE: OBJECT_TYPE,
    PROPERTIES: {
        LOCATION_PROPERTIES: {
            TYPE: STR_TYPE,
            DESCRIPTION: LOCATION_PROPERTIES_DESC
        },
        UNIT_PROPERTIES: {
            TYPE: STR_TYPE,
        }
    },
    REQUIRED: [LOCATION_PROPERTIES]
}
WEATHER_FN = {
    NAME: WEATHER_FN_NAME,
    DESCRIPTION: WEATHER_FN_DESC,
    PARAMETERS: WEATHER_FN_PARAM
}
WEATHER_TOOL = {
    TYPE: FUNCTION_TYPE,
    FUNCTION_TYPE: WEATHER_FN
}

PAPER_FN_NAME = "查询论文"
PAPER_FN_DESC = "论文名字"
PAPER_FN_PARAM = {
    TYPE: OBJECT_TYPE,
    PROPERTIES: {
        LOCATION_PROPERTIES: {
            TYPE: STR_TYPE,
            DESCRIPTION: LOCATION_PROPERTIES_DESC
        },
        UNIT_PROPERTIES: {
            TYPE: STR_TYPE,
        }
    },
    REQUIRED: [TITLE_PROPERTIES]
}
PAPER_FN = {
    NAME: PAPER_FN_NAME,
    DESCRIPTION: PAPER_FN_DESC,
    PARAMETERS: PAPER_FN_PARAM
}
PAPER_TOOL = {
    TYPE: FUNCTION_TYPE,
    FUNCTION_TYPE: PAPER_FN
}


class TestMessageUtils(unittest.TestCase):
    def test_has_chinese_chars(self):
        self.assertTrue(has_chinese_chars("全中文字符"))
        self.assertTrue(has_chinese_chars("Part 中文字符"))
        self.assertFalse(has_chinese_chars("ALL ENGLISH"))

    def test_get_function_description(self):
        function = WEATHER_FN
        tool_desc = '### {name_for_human}\n\n{name_for_model}: ' \
                    '{description_for_model} 输入参数：{parameters} {args_format}'
        tool_desc = tool_desc.format(name_for_human=function.get(NAME, None),
                                     name_for_model=function.get(NAME, None),
                                     description_for_model=function.get(DESCRIPTION, None),
                                     parameters=json.dumps(function.get(PARAMETERS, None),
                                                           ensure_ascii=False),
                                     args_format=EMPTY_STR).rstrip()
        self.assertEqual(get_function_description(function), tool_desc)

    def test_prepend_fncall_system(self):
        functions = [
            WEATHER_FN,
            PAPER_FN
        ]

        user_message = {
            'role': 'user',
            'content': "深圳的天气怎么样？"
        }
        fncall_model = DefaultFnCallModel()
        messages = [ChatMessage(**user_message)]
        fncall_sys_messages = prepend_fncall_system(fncall_model, messages, functions)

        # Create true answer
        tool_descs = '\n\n'.join(get_function_description(function) for function in functions)
        tool_names = ','.join(
            function.get(NAME, function.get('name_for_model', ''))
            for function in functions)
        tool_system = fncall_model.fn_call_template_zh.format(tool_descs=tool_descs, tool_names=tool_names)
        messages.insert(0, ChatMessage(role="system", content="You are a helpful assitant"))
        messages[0].content += tool_system
        self.assertEqual(fncall_sys_messages, messages)

    def test_prepend_tools_system(self):
        functions = [
            WEATHER_TOOL,
            PAPER_TOOL
        ]
        tools = [
            ToolCall(**functions[0]),
            ToolCall(**functions[1])
        ]

        user_message = {
            'role': 'user',
            'content': "深圳的天气怎么样？"
        }
        fncall_model = DefaultFnCallModel()
        messages = [ChatMessage(**user_message)]
        fncall_sys_messages = prepend_tools_system(fncall_model, messages, tools)

        # Create true answer
        tool_descs = '\n\n'.join(get_function_description(function.get("function", '')) for function in functions)
        tool_names = ','.join(
            function.get(NAME, function.get('name_for_model', ''))
            for function in functions)
        tool_system = fncall_model.fn_call_template_zh.format(tool_descs=tool_descs, tool_names=tool_names)
        messages.insert(0, ChatMessage(role="system", content="You are a helpful assitant"))
        messages[0].content += tool_system

        self.assertEqual(fncall_sys_messages, messages)

    def test_postprocess_fncall_messages(self):
        fn_call_model = QwenFnCallModel()
        message = ChatMessage(role="assistant", content='✿FUNCTION✿: 查询天气\n✿ARGS✿: {"location": 深圳}')
        fncall_messages = postprocess_fncall_messages(fn_call_model, message)
        true_fncall_messages = ChatMessage(
            role=message['role'],
            content=EMPTY_STR,
            tool_calls=[ToolCallResponse(
                id=fncall_messages.tool_calls[0].id,
                type=FUNCTION_TYPE,
                function=FunctionCall(
                    name="查询天气",
                    arguments='{"location": 深圳}'
                )
            )]
        )
        self.assertEqual(true_fncall_messages, fncall_messages)


class TestEnvUtils(unittest.TestCase):
    def test_engine_select(self):
        torch_module_str = 'torch'
        torch_cuda_module_str = 'torch.cuda'
        torch_npu_module_str = 'torch_npu'
        sys.modules[torch_module_str] = MagicMock()
        sys.modules[torch_cuda_module_str] = MagicMock()
        sys.modules[torch_npu_module_str] = None

        import torch
        torch.cuda.is_available.return_value = True
        self.assertEqual(engine_select(), 'GPU')
        torch.cuda.is_available.return_value = False
        self.assertEqual(engine_select(), None)

        sys.modules[torch_module_str] = None
        sys.modules[torch_npu_module_str] = MagicMock()
        self.assertEqual(engine_select(), 'NPU')

        sys.modules[torch_module_str] = None
        sys.modules[torch_npu_module_str] = None
        self.assertEqual(engine_select(), None)


if __name__ == '__main__':
    unittest.main()
