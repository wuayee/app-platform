/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.model.openai;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fitframework.flowable.Choir;
import com.huawei.jade.fel.chat.ChatOptions;
import com.huawei.jade.fel.chat.MessageType;
import com.huawei.jade.fel.chat.protocol.ChatCompletion;
import com.huawei.jade.fel.chat.protocol.FlatChatMessage;
import com.huawei.jade.fel.embed.EmbedOptions;
import com.huawei.jade.fel.embed.EmbedRequest;
import com.huawei.jade.fel.embed.EmbedResponse;
import com.huawei.jade.fel.model.openai.client.OpenAiClient;
import com.huawei.jade.fel.model.openai.service.OpenAiChatModelService;
import com.huawei.jade.fel.model.openai.service.OpenAiChatModelStreamService;
import com.huawei.jade.fel.model.openai.service.OpenAiEmbedModelService;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * OpenAI 客户端服务测试。
 *
 * @author 张庭怿
 * @since 2024-05-17
 */
public class OpenAiModelServiceTest {
    private MockWebServer server;

    private OpenAiClient client;

    @BeforeEach
    public void setUp() throws IOException {
        this.server = new MockWebServer();
        this.client = new OpenAiClient("http://localhost:" + this.server.getPort(), false);
    }

    @AfterEach
    public void tearDown() throws IOException {
        this.server.shutdown();
    }

    @Test
    public void testOpenAiChatModelService() {
        String content = "test";
        this.server.enqueue(new MockResponse().setBody(this.getMockChatCompletionResponseBody(content)));
        OpenAiChatModelService service = new OpenAiChatModelService(this.client);
        FlatChatMessage message = service.generate(this.getRequest());
        assertThat(message).extracting(FlatChatMessage::getText).isEqualTo(content);
    }

    @Test
    public void testOpenAiEmbedModelService() {
        List<Float> vector = Arrays.asList(0.1F, 0.2F, 0.3F);
        this.server.enqueue(new MockResponse().setBody(this.getMockEmbeddingResponseBody(vector)));

        EmbedOptions options = new EmbedOptions();
        options.setModel("model");
        EmbedRequest request = new EmbedRequest();
        request.setOptions(options);
        request.setInputs(Arrays.asList("test"));

        OpenAiEmbedModelService service = new OpenAiEmbedModelService(this.client);
        EmbedResponse response = service.generate(request);
        assertThat(response.getEmbeddings()).hasSize(1).containsExactly(vector);
    }

    @Test
    public void testOpenAiChatModelStreamService() {
        List<String> contents = Arrays.asList("1", "2", "3");
        this.server.enqueue(new MockResponse().setBody(this.getMockStreamResponseBody(contents)));
        OpenAiChatModelStreamService service = new OpenAiChatModelStreamService(this.client);
        Choir<FlatChatMessage> choir = service.generate(this.getRequest());
        List<FlatChatMessage> response = choir.blockAll();
        assertThat(response).extracting(FlatChatMessage::getText).isEqualTo(contents);
    }

    private ChatCompletion getRequest() {
        FlatChatMessage msg = new FlatChatMessage();
        msg.setType(MessageType.HUMAN);
        msg.setText("test");

        ChatOptions options = new ChatOptions();
        options.setModel("model");

        List<FlatChatMessage> messages = new ArrayList<>();
        messages.add(msg);

        ChatCompletion request = new ChatCompletion();
        request.setMessages(messages);
        request.setOptions(options);
        return request;
    }

    private String getMockChatCompletionResponseBody(String content) {
        return "{\"id\": \"0\","
                + "\"object\": \"chat.completion\","
                + "\"created\": 0,"
                + "\"model\": \"test_model\","
                + "\"choices\": [{\"index\": 0,"
                + "\"message\": {\"role\": \"assistant\",\"content\": \"" + content + "\"},"
                + "\"finish_reason\": \"length\"}],"
                + "\"usage\": {\"prompt_tokens\": 0,\"completion_tokens\": 0,\"total_tokens\": 0}}";
    }

    private String getMockEmbeddingResponseBody(List<Float> vector) {
        String embeddings = vector.stream().map(String::valueOf).collect(Collectors.joining(","));
        return "{\"object\": \"list\","
                + "\"data\": [{\"index\": 0,"
                + "\"object\": \"embedding\",\"embedding\": [" + embeddings + "]}],"
                + "\"model\": \"bce-embedding-base\","
                + "\"usage\": {\"prompt_tokens\": 0,\"total_tokens\": 0}}";
    }

    private String getMockStreamResponseBody(List<String> contents) {
        StringBuilder sb = new StringBuilder();
        contents.forEach(content -> sb.append(this.getMockStreamResponseChunk(content)));
        sb.append("data: [DONE]\n");
        return sb.toString();
    }

    private String getMockStreamResponseChunk(String content) {
        return "data: {\"id\": \"0\","
                + "\"object\": \"chat.completion.chunk\","
                + "\"created\": 0,"
                + "\"model\": \"test_model\","
                + "\"choices\": [{\"index\": 0,\"delta\": {\"content\": \"" + content + "\"},"
                + "\"finish_reason\": null}]}\n\n";
    }
}
