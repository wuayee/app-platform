/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway;

import static org.junit.Assert.assertEquals;

import com.huawei.jade.model.service.gateway.entity.ChatMessage;
import com.huawei.jade.model.service.gateway.entity.FunctionCall;
import com.huawei.jade.model.service.gateway.entity.FunctionCallModel;
import com.huawei.jade.model.service.gateway.entity.FunctionDefinition;
import com.huawei.jade.model.service.gateway.entity.ToolCall;
import com.huawei.jade.model.service.gateway.entity.ToolCallResponse;
import com.huawei.jade.model.service.gateway.utils.ModelMessageUtilis;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 工具方法集合测试。
 *
 * @author 陈思远
 * @since 2024-07-05
 */
public class ModelMessageUtilisTest {
    @Test
    public void testPrependToolsSystem() {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("user", Optional.of("What can I do with these functions?"), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty()));

        String separator = System.lineSeparator();

        StringBuilder toolsInfoEn = new StringBuilder();
        toolsInfoEn.append("## Tools Information").append(separator).append(separator)
                .append("{tool_descs}Use the following tool names: {tool_names}");

        StringBuilder toolsInfoZh = new StringBuilder();
        toolsInfoZh.append("## 工具信息").append(separator).append(separator).append("{tool_descs}").append(separator)
                .append(separator).append("请使用以下工具名称: {tool_names}");

        StringBuilder sb = new StringBuilder();
        sb.append("You are a helpful assistant").append(separator).append("# Tools").append(separator).append(separator)
                .append("## You have access to the following tools:").append(separator).append(separator)
                .append("### FunctionA").append(separator).append(separator)
                .append("FunctionA: Description for FunctionA Parameters: {\"param1\":\"value1\"}").append(separator)
                .append(separator).append("### FunctionB").append(separator).append(separator)
                .append("FunctionB: Description for FunctionB Parameters: {\"param2\":\"value2\"}").append(separator)
                .append(separator).append("## When you need to call a tool, please insert the following command")
                .append(" in your reply, which can be called zero or multiple times according to your needs:")
                .append(separator).append("✿FUNCTION✿: The tool to use, should be one of [FunctionA,FunctionB].")
                .append(separator).append("✿ARGS✿: Tool input").append(separator)
                .append("✿RESULT✿: The result returned by the tool. The image needs to be rendered as ![](url).")
                .append(separator).append("✿RETURN✿: Reply based on the tool result");

        Map<String, Object> parameters1 = new HashMap<>();
        parameters1.put("param1", "value1");
        FunctionDefinition funcDef1 = new FunctionDefinition("FunctionA", "Description for FunctionA",
                Optional.of(parameters1));

        Map<String, Object> parameters2 = new HashMap<>();
        parameters2.put("param2", "value2");
        FunctionDefinition funcDef2 = new FunctionDefinition("FunctionB", "Description for FunctionB",
                Optional.of(parameters2));

        ToolCall toolCall1 = new ToolCall("function", funcDef1);
        ToolCall toolCall2 = new ToolCall("function", funcDef2);
        List<ToolCall> tools = Arrays.asList(toolCall1, toolCall2);
        FunctionCallModel functionCallModel = new FunctionCallModel();
        List<ChatMessage> resultMessages = ModelMessageUtilis.prependToolsSystem(functionCallModel, messages, tools);
        assertEquals(sb.toString(), resultMessages.get(0).getContent().orElse(""));
    }

    /**
     * preprocessFncallMessages方法的测试单元用例,role为user和tool
     *
     * @author 陈思远
     * @since 2024-07-05
     */
    @Test
    public void testBasicInput() {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("user", Optional.of("System message：\"What can I do with these functions?\""),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
        messages.add(new ChatMessage("tool", Optional.of("User message：\"What can I do with these functions?\""),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
        // 获取系统行分隔符
        String sep = System.lineSeparator();
        // 设置FncallModel
        FunctionCallModel functionCallModel = new FunctionCallModel();

        List<ChatMessage> result = ModelMessageUtilis.preprocessFncallMessages(functionCallModel, messages);
        StringBuilder expectedMessage = new StringBuilder();
        expectedMessage.append("System message：\"What can I do with these functions?\"").append(sep)
                .append("✿RESULT✿: User message：\"What can I do with these functions?\"").append(sep)
                .append("✿RETURN✿: ");
        assertEquals(expectedMessage.toString(), result.get(0).getContent().orElse(""));
    }

    /**
     * preprocessFncallMessages方法的测试单元用例,role为user和assistant和tool
     *
     * @author 陈思远
     * @since 2024-07-05
     */
    @Test
    public void testPreprocessFncallMessages() {
        // 定义函数和工具
        Map<String, Object> parameters1 = new HashMap<>();
        parameters1.put("param1", "value1");
        FunctionDefinition funcDef1 = new FunctionDefinition("FunctionA", "Description for FunctionA",
                Optional.of(parameters1));

        Map<String, Object> parameters2 = new HashMap<>();
        parameters2.put("param2", "value2");
        FunctionDefinition funcDef2 = new FunctionDefinition("FunctionB", "Description for FunctionB",
                Optional.of(parameters2));

        // 构建toolCalls
        FunctionCall function1 = new FunctionCall("functionA", "arg1=value1");
        FunctionCall function2 = new FunctionCall("functionB", "arg2=value2");

        ToolCallResponse toolResponse1 = new ToolCallResponse("1", "type1", function1);
        ToolCallResponse toolResponse2 = new ToolCallResponse("2", "type2", function2);

        List<ToolCallResponse> toolCallResponseList = new ArrayList<>();
        toolCallResponseList.add(toolResponse1);
        toolCallResponseList.add(toolResponse2);

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("user", Optional.of("\"Hello\""), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty()));
        messages.add(new ChatMessage("assistant", Optional.of("\"What can I do with these functions?\""),
                Optional.of("assistantMessage"), Optional.empty(), Optional.of("myToolChoice"),
                Optional.of(toolCallResponseList)));
        messages.add(new ChatMessage("tool", Optional.of("\"The weather in New York is sunny and 75 degrees.\""),
                Optional.of("toolMessage"), Optional.empty(), Optional.of("myToolChoice"),
                Optional.of(toolCallResponseList)));
        // 获取系统行分隔符
        String sep = System.lineSeparator();
        // 设置FncallModel
        FunctionCallModel functionCallModel = new FunctionCallModel();

        List<ChatMessage> result = ModelMessageUtilis.preprocessFncallMessages(functionCallModel, messages);
        StringBuilder expectedMessage = new StringBuilder();
        expectedMessage.append("\"Hello\"").append(sep).append(sep).append("\"What can I do with these functions?\"")
                .append(sep).append("✿FUNCTION✿: functionA").append(sep).append("✿ARGS✿: arg1=value1").append(sep)
                .append("✿RESULT✿: \"The weather in New York is sunny and 75 degrees.\"").append(sep)
                .append("✿RETURN✿");
        assertEquals(expectedMessage.toString(), result.get(0).getContent().orElse(""));
    }

    /**
     * preprocessFncallMessages方法的测试单元用例
     *
     * @author 王浩冉
     * @since 2024-07-24
     */
    @Test
    public void testpostprocessFncallMessages() {
        ChatMessage message = new ChatMessage("assistant",
                Optional.of("✿FUNCTION✿: 查询天气 ✿ARGS✿: {\"location\": 深圳}"),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        FunctionCallModel fncall = new FunctionCallModel();
        ChatMessage resultMessage = ModelMessageUtilis.postprocessFncallMessages(fncall, message);
        String id = resultMessage.getToolCalls().get().get(0).getId();
        List<ToolCallResponse> tools = new ArrayList<>();
        tools.add(new ToolCallResponse(id, "function", new FunctionCall("查询天气", "{\"location\": 深圳}")));
        ChatMessage expectedMessage = new ChatMessage("assistant", Optional.of(""), Optional.of(""), Optional.empty(),
                Optional.of(""), Optional.of(tools));
        assertEquals(expectedMessage, resultMessage);
    }
}
