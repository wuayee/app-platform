/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.model.openai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.huawei.fitframework.resource.web.Media;
import com.huawei.jade.fel.chat.character.HumanMessage;
import com.huawei.jade.fel.chat.character.SystemMessage;
import com.huawei.jade.fel.chat.protocol.FlatChatMessage;
import com.huawei.jade.fel.model.openai.entity.Usage;
import com.huawei.jade.fel.model.openai.entity.chat.OpenAiChatCompletionRequest;
import com.huawei.jade.fel.model.openai.entity.chat.OpenAiChatCompletionResponse;
import com.huawei.jade.fel.model.openai.entity.chat.message.OpenAiChatMessage;
import com.huawei.jade.fel.model.openai.entity.chat.message.Role;
import com.huawei.jade.fel.model.openai.entity.chat.message.content.UserContent;
import com.huawei.jade.fel.model.openai.entity.chat.message.tool.OpenAiToolCall;
import com.huawei.jade.fel.model.openai.utils.OpenAiMessageUtils;
import com.huawei.jade.fel.tool.Tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * {@link OpenAiChatMessage} 序列化与反序列化相关的单元测试。
 *
 * @author 张庭怿
 * @since 2024-4-30
 */
public class OpenAiChatMessageTest {
    @Test
    void testSystemMessageSerialization() throws JsonProcessingException {
        OpenAiChatMessage message = OpenAiChatMessage.builder()
                .role(Role.SYSTEM.name())
                .content("test")
                .build();
        String json = "{\"role\":\"system\",\"content\":\"test\"}";
        assertEquals(json, OpenAiMessageUtils.OBJECT_MAPPER.writeValueAsString(message));
    }

    @Test
    void testBasicUserMessageSerialization() throws JsonProcessingException {
        OpenAiChatMessage message = OpenAiChatMessage.builder()
                .role(Role.USER.name())
                .content("test")
                .build();
        String json = "{\"role\":\"user\",\"content\":\"test\"}";
        assertEquals(json, OpenAiMessageUtils.OBJECT_MAPPER.writeValueAsString(message));
    }

    @Test
    void testBasicUserMessageDeserialization() throws JsonProcessingException {
        String json = "{\"role\":\"user\",\"content\":\"test\"}";
        OpenAiChatMessage message = OpenAiMessageUtils.OBJECT_MAPPER.readValue(json, OpenAiChatMessage.class);
        assertThat(message).hasFieldOrPropertyWithValue("role", Role.USER)
                .hasFieldOrPropertyWithValue("content", "test");
    }

    @Test
    void testTextOnlyUserContentSerialization() throws JsonProcessingException {
        UserContent content = UserContent.text("test");
        String json = "{\"type\":\"text\",\"text\":\"test\"}";
        assertEquals(json, OpenAiMessageUtils.OBJECT_MAPPER.writeValueAsString(content));
    }

    @Test
    void testImageUserContentSerialization() throws JsonProcessingException {
        UserContent content = UserContent.image("url");
        String json = "{\"type\":\"image_url\",\"image_url\":{\"url\":\"url\"}}";
        assertEquals(json, OpenAiMessageUtils.OBJECT_MAPPER.writeValueAsString(content));
    }

    @Test
    void testUserMessageWithImageSerialization() throws JsonProcessingException {
        UserContent textContent = UserContent.text("test");
        UserContent imageContent = UserContent.image("url");
        List<UserContent> content = Arrays.asList(textContent, imageContent);
        OpenAiChatMessage message = OpenAiChatMessage.builder()
                .role(Role.USER.name())
                .content(content)
                .build();
        String json = "{"
                + "\"role\":\"user\","
                + "\"content\":"
                + "[{\"type\":\"text\",\"text\":\"test\"},"
                + "{\"type\":\"image_url\",\"image_url\":{\"url\":\"url\"}}"
                + "]}";
        assertEquals(json, OpenAiMessageUtils.OBJECT_MAPPER.writeValueAsString(message));
    }

    @Test
    void testAssistantMessageBasicSerialization() throws JsonProcessingException {
        OpenAiChatMessage message = OpenAiChatMessage.builder()
                .role(Role.ASSISTANT.name())
                .content("test")
                .build();
        String json = "{\"role\":\"assistant\",\"content\":\"test\"}";
        assertEquals(json, OpenAiMessageUtils.OBJECT_MAPPER.writeValueAsString(message));
    }

    @Test
    void testAssistantMessageBasicDeserialization() throws JsonProcessingException {
        String json = "{\"role\":\"assistant\",\"content\":\"test\"}";
        OpenAiChatMessage message = OpenAiMessageUtils.OBJECT_MAPPER.readValue(json, OpenAiChatMessage.class);
        assertThat(message).hasFieldOrPropertyWithValue("role", Role.ASSISTANT)
                .hasFieldOrPropertyWithValue("content", "test");
    }

    @Test
    void testToolCallSerialization() throws JsonProcessingException {
        OpenAiToolCall.FunctionCall functionCall = new OpenAiToolCall.FunctionCall();
        functionCall.setName("test_function");
        functionCall.setArguments("test_arguments");

        OpenAiToolCall toolCall = new OpenAiToolCall();
        toolCall.setId("test_id");
        toolCall.setType("function");
        toolCall.setFunction(functionCall);

        String json = "{"
                + "\"id\":\"test_id\","
                + "\"type\":\"function\","
                + "\"function\":"
                + "{\"name\":\"test_function\",\"arguments\":\"test_arguments\"}"
                + "}";
        assertEquals(json, OpenAiMessageUtils.OBJECT_MAPPER.writeValueAsString(toolCall));
    }

    @Test
    void testToolCallDeserialization() throws JsonProcessingException {
        String json = "{"
                + "\"id\":\"test_id\","
                + "\"type\":\"function\","
                + "\"function\":"
                + "{\"name\":\"test_function\",\"arguments\":\"test_arguments\"}"
                + "}";
        OpenAiToolCall toolCall = OpenAiMessageUtils.OBJECT_MAPPER.readValue(json, OpenAiToolCall.class);
        assertThat(toolCall).hasFieldOrPropertyWithValue("id", "test_id")
                .hasFieldOrPropertyWithValue("type", "function");
        assertThat(toolCall.getFunction()).hasFieldOrPropertyWithValue("name", "test_function")
                .hasFieldOrPropertyWithValue("arguments", "test_arguments");
    }

    @Test
    void testAssistantMessageWithToolCallsSerialization() throws JsonProcessingException {
        OpenAiToolCall.FunctionCall functionCall = new OpenAiToolCall.FunctionCall();
        functionCall.setName("test_function");
        functionCall.setArguments("test_arguments");

        OpenAiToolCall toolCall = new OpenAiToolCall();
        toolCall.setId("test_id");
        toolCall.setType("function");
        toolCall.setFunction(functionCall);

        OpenAiChatMessage message = OpenAiChatMessage.builder()
                .role(Role.ASSISTANT.name())
                .content("test")
                .toolCalls(Collections.singletonList(toolCall))
                .build();
        String json = "{"
                + "\"role\":\"assistant\",\"content\":\"test\","
                + "\"tool_calls\":[{"
                + "\"id\":\"test_id\",\"type\":\"function\","
                + "\"function\":"
                + "{\"name\":\"test_function\",\"arguments\":\"test_arguments\"}"
                + "}]}";
        assertEquals(json, OpenAiMessageUtils.OBJECT_MAPPER.writeValueAsString(message));
    }

    @Test
    void testAssistantMessageWithToolCallsDeserialization() throws JsonProcessingException {
        String json = "{"
                + "\"role\":\"assistant\",\"content\":\"test\","
                + "\"tool_calls\":[{"
                + "\"id\":\"test_id\","
                + "\"type\":\"function\","
                + "\"function\":"
                + "{\"name\":\"test_function\","
                + "\"arguments\":\"test_arguments\"}}]"
                + "}";
        OpenAiChatMessage message = OpenAiMessageUtils.OBJECT_MAPPER.readValue(json, OpenAiChatMessage.class);
        assertThat(message).hasFieldOrPropertyWithValue("role", Role.ASSISTANT)
                .hasFieldOrPropertyWithValue("content", "test");

        OpenAiToolCall toolCall = message.getToolCalls().get(0);
        assertThat(toolCall).hasFieldOrPropertyWithValue("id", "test_id")
                .hasFieldOrPropertyWithValue("type", "function");
        assertThat(toolCall.getFunction()).hasFieldOrPropertyWithValue("name", "test_function")
                .hasFieldOrPropertyWithValue("arguments", "test_arguments");
    }


    @Test
    void testToolMessageSerialization() throws JsonProcessingException {
        OpenAiChatMessage message = OpenAiChatMessage.builder()
                .role(Role.TOOL.name())
                .content("test")
                .toolCallId("test_id")
                .build();
        String json = "{\"role\":\"tool\",\"content\":\"test\",\"tool_call_id\":\"test_id\"}";
        assertEquals(json, OpenAiMessageUtils.OBJECT_MAPPER.writeValueAsString(message));
    }

    @Test
    void testBasicMessageConversion() {
        FlatChatMessage systemMessage = FlatChatMessage.from(new SystemMessage("system message"));
        FlatChatMessage humanMessage = FlatChatMessage.from(new HumanMessage("human message"));

        List<OpenAiChatMessage> prompts = OpenAiMessageUtils.buildPrompts(Arrays.asList(systemMessage, humanMessage));
        assertThat(prompts.get(0)).hasFieldOrPropertyWithValue("role", Role.SYSTEM)
                .hasFieldOrPropertyWithValue("content", "system message");
        assertThat(prompts.get(1)).hasFieldOrPropertyWithValue("role", Role.USER)
                .hasFieldOrPropertyWithValue("content", "human message");
    }

    @Test
    void testMessageWithImageUrlConversion() {
        FlatChatMessage humanMessage = FlatChatMessage.from(new HumanMessage("human message"));
        Media media = new Media("image/jpg", "test_base64");
        humanMessage.setMedias(Collections.singletonList(media));

        List<OpenAiChatMessage> prompts = OpenAiMessageUtils.buildPrompts(Collections.singletonList(humanMessage));
        List<UserContent> contents = (List<UserContent>) prompts.get(0).getContent();
        assertThat(contents.get(0)).extracting(UserContent::getType, content -> content.getImageUrl().getUrl())
                .containsExactly("image_url", "data:image/jpg;base64,test_base64");
        assertThat(contents.get(1)).extracting(UserContent::getType, UserContent::getText)
                .containsExactly("text", "human message");
    }

    @Test
    void testToolsConversion() throws JsonProcessingException {
        String json = "{\"name\": \"get_current_weather\","
                + "\"description\": \"Get the current weather in a given location\","
                + "\"parameters\": {"
                + "\"type\": \"object\","
                + "\"properties\": {"
                + "\"location\": {"
                + "\"type\": \"string\","
                + "\"description\": \"The city and state, e.g. San Francisco, CA\""
                + "},"
                + "\"unit\": {"
                + "\"type\": \"string\","
                + "\"enum\": [\"celsius\", \"fahrenheit\"]}},"
                + "\"required\": [\"location\"]"
                + "}}";
        Map<String, Object> map =
                OpenAiMessageUtils.OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        Tool tool = new Tool();
        tool.setSchema(map);
        String toolsJson = "[{\"type\":\"function\","
                + "\"function\":{"
                + "\"name\":\"get_current_weather\","
                + "\"description\":\"Get the current weather in a given location\","
                + "\"parameters\":"
                + "{\"type\":\"object\","
                + "\"properties\":"
                + "{\"location\":"
                + "{\"type\":\"string\","
                + "\"description\":\"The city and state, e.g. San Francisco, CA\"},"
                + "\"unit\":{\""
                + "type\":\"string\","
                + "\"enum\":[\"celsius\",\"fahrenheit\"]}},"
                + "\"required\":[\"location\"]}}"
                + "}]";
        assertEquals(toolsJson,
                OpenAiMessageUtils.OBJECT_MAPPER.writeValueAsString(
                        OpenAiMessageUtils.buildTools(Collections.singletonList(tool))));
    }

    @Test
    void testOpenAiChatCompletionRequestSerialization() throws JsonProcessingException {
        FlatChatMessage humanMessage = FlatChatMessage.from(new HumanMessage("human"));
        Media media = new Media("image/jpg", "test_base64");
        humanMessage.setMedias(Collections.singletonList(media));

        String toolJson = "{\"name\": \"test_tool\",\"description\": \"tool_description\","
                + "\"parameters\": {\"type\": \"object\","
                + "\"properties\": {\"location\": {\"type\": \"string\","
                + "\"description\": \"location_description\"},"
                + "\"unit\": {\"type\": \"string\",\"enum\": [\"celsius\", \"fahrenheit\"]}},"
                + "\"required\": [\"location\"]}}";
        Map<String, Object> map =
                OpenAiMessageUtils.OBJECT_MAPPER.readValue(toolJson, new TypeReference<Map<String, Object>>() {});
        Tool tool = new Tool();
        tool.setSchema(map);
        String requestJson = "{\"messages\":[{\"role\":\"user\","
                + "\"content\":[{\"type\":\"image_url\",\"image_url\":{\"url\":\"data:image/jpg;base64,test_base64\"}},"
                + "{\"type\":\"text\",\"text\":\"human\"}]}],"
                + "\"model\":\"test_model\",\"stream\":false,\"temperature\":0.2,\"frequency_penalty\":1.0,"
                + "\"max_tokens\":512,\"presence_penalty\":1.0,\"top_p\":0.1,"
                + "\"tools\":[{\"type\":\"function\","
                + "\"function\":{\"name\":\"test_tool\",\"description\":\"tool_description\","
                + "\"parameters\":{\"type\":\"object\",\"properties\":{\"location\":{\"type\":\"string\","
                + "\"description\":\"location_description\"},\"unit\":{\"type\":\"string\","
                + "\"enum\":[\"celsius\",\"fahrenheit\"]}},\"required\":[\"location\"]}}}],"
                + "\"tool_choice\":\"auto\"}";
        OpenAiChatCompletionRequest request = OpenAiChatCompletionRequest.builder()
                .messages(OpenAiMessageUtils.buildPrompts(Collections.singletonList(humanMessage)))
                .model("test_model").frequencyPenalty(1.0).maxTokens(512).presencePenalty(1.0)
                .stream(false).temperature(0.2).topP(0.1)
                .tools(OpenAiMessageUtils.buildTools(Collections.singletonList(tool))).toolChoice("auto")
                .build();
        assertEquals(requestJson, OpenAiMessageUtils.OBJECT_MAPPER.writeValueAsString(request));
    }

    @Test
    void testOpenAiChatCompletionResponseDeserialization() throws JsonProcessingException {
        String responseJson = "{\"id\":\"abc123\","
                + "\"object\":\"chat.completion\",\"created\":123,"
                + "\"model\":\"test_model\",\"choices\":[{"
                + "\"index\":0,\"message\":"
                + "{\"role\":\"assistant\","
                + "\"content\":\"test\","
                + "\"tool_calls\":[{"
                + "\"id\":\"call_abc123\",\"type\":\"function\","
                + "\"function\":{\"name\":\"test_function_name\",\"arguments\":\"test_arguments\"}}]},"
                + "\"logprobs\":null,\"finish_reason\":\"tool_calls\"}],"
                + "\"usage\":{\"prompt_tokens\":12,\"completion_tokens\":34,\"total_tokens\":56}}";
        OpenAiChatCompletionResponse response =
                OpenAiMessageUtils.OBJECT_MAPPER.readValue(responseJson, OpenAiChatCompletionResponse.class);

        OpenAiChatMessage message = response.getChoices().get(0).getMessage(); // 构造的JSON字符串中只有一个choice
        OpenAiToolCall toolCall = message.getToolCalls().get(0);
        OpenAiToolCall.FunctionCall functionCall = toolCall.getFunction();
        assertThat(message).extracting(OpenAiChatMessage::getRole, OpenAiChatMessage::getContent)
                        .containsExactly(Role.ASSISTANT, "test");
        assertThat(toolCall).extracting(OpenAiToolCall::getId, OpenAiToolCall::getType)
                        .containsExactly("call_abc123", "function");
        assertThat(functionCall)
                .extracting(OpenAiToolCall.FunctionCall::getName, OpenAiToolCall.FunctionCall::getArguments)
                .containsExactly("test_function_name", "test_arguments");

        Usage usage = response.getUsage();
        assertThat(usage).extracting(Usage::getPromptTokens, Usage::getCompletionTokens, Usage::getTotalTokens)
                .containsExactly(12, 34, 56);
    }
}
