# -*- coding: utf-8 -*-
# !/usr/bin/python
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
import textwrap


class FunctionCallFactory():
    @staticmethod
    def get_fncall_model(model_name: str):
        if "qwen" in model_name.lower():
            return QwenFnCallModel()
        return DefaultFnCallModel()


class QwenFnCallModel():
    def __init__(self):
        self.fn_name = '✿FUNCTION✿'
        self.fn_args = '✿ARGS✿'
        self.fn_result = '✿RESULT✿'
        self.fn_exit = '✿RETURN✿'
        self.fn_stop_words = [self.fn_result, f'{self.fn_result}:', f'{self.fn_result}:\n']
        self.fn_call_template_zh = textwrap.dedent("""

        # 工具

        ## 你拥有如下工具：

        {tool_descs}

        ## 你可以在回复中插入零次、一次或多次以下命令以调用工具：

        %s: 工具名称，必须是[{tool_names}]之一。
        %s: 工具输入
        %s: 工具结果，需将图片用![](url)渲染出来。
        %s: 根据工具结果进行回复""" % (
            self.fn_name,
            self.fn_args,
            self.fn_result,
            self.fn_exit,
        ))

        self.fn_call_template_en = textwrap.dedent("""

        # Tools

        ## You have access to the following tools:

        {tool_descs}

        ## When you need to call a tool, please insert the following command in your reply, which can be called zero or multiple times according to your needs:

        %s: The tool to use, should be one of [{tool_names}]
        %s: The input of the tool
        %s: The result returned by the tool. The image needs to be rendered as ![](url)
        %s: Reply based on tool result""" % (
            self.fn_name,
            self.fn_args,
            self.fn_result,
            self.fn_exit,
        ))

    @staticmethod
    def remove_special_tokens(text: str, strip: bool = True) -> str:
        text = text.replace('✿:', '✿')
        text = text.replace('✿：', '✿')
        out = ''
        is_special = False
        for c in text:
            if c == '✿':
                is_special = not is_special
                continue
            if is_special:
                continue
            out += c
        if strip:
            out = out.lstrip('\n').rstrip()
        return out


class DefaultFnCallModel():
    def __init__(self):
        self.fn_name = ''
        self.fn_args = ''
        self.fn_result = ''
        self.fn_exit = ''
        self.fn_stop_words = [self.fn_result, f'{self.fn_result}:', f'{self.fn_result}:\n']
        self.fn_call_template_zh = textwrap.dedent("""

        # 工具

        ## 你拥有如下工具：

        {tool_descs}

        ## 你可以在回复中插入零次、一次或多次以下命令以调用工具：

        %s: 工具名称，必须是[{tool_names}]之一。
        %s: 工具输入
        %s: 工具结果，需将图片用![](url)渲染出来。
        %s: 根据工具结果进行回复""" % (
            self.fn_name,
            self.fn_args,
            self.fn_result,
            self.fn_exit,
        ))

        self.fn_call_template_en = textwrap.dedent("""

        # Tools

        ## You have access to the following tools:

        {tool_descs}

        ## When you need to call a tool, please insert the following command in your reply, which can be called zero or multiple times according to your needs:

        %s: The tool to use, should be one of [{tool_names}]
        %s: The input of the tool
        %s: The result returned by the tool. The image needs to be rendered as ![](url)
        %s: Reply based on tool result""" % (
            self.fn_name,
            self.fn_args,
            self.fn_result,
            self.fn_exit,
        ))

    @staticmethod
    def remove_special_tokens(text: str, strip: bool = True) -> str:
        return text
