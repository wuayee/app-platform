/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.model.openai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import modelengine.fel.chat.ChatOptions;
import modelengine.fel.chat.character.HumanMessage;
import modelengine.fel.chat.protocol.ChatCompletion;
import modelengine.fel.chat.protocol.FlatChatMessage;
import modelengine.fel.embed.EmbedOptions;
import modelengine.fel.embed.EmbedRequest;
import modelengine.fel.embed.EmbedResponse;
import modelengine.fel.model.openai.client.OpenAiClient;
import com.huawei.jade.fel.model.openai.client.OpenAiClientSse;
import modelengine.fel.model.openai.entity.chat.OpenAiChatCompletionRequest;
import modelengine.fel.model.openai.entity.chat.message.OpenAiChatMessage;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.okhttp.OkHttpClassicClientFactory;
import modelengine.fit.security.Decryptor;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.conf.Config;
import modelengine.fitframework.conf.support.ReadonlyMapConfig;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * OpenAI 客户端服务测试。
 *
 * @author 张庭怿
 * @since 2024-05-17
 */
@FitTestWithJunit(includeClasses = {OpenAiClientSse.class, OkHttpClassicClientFactory.class})
public class OpenAiModelServiceTest {
    private static final ObjectSerializer SERIALIZER = new JacksonObjectSerializer(null, null, null);

    private MockWebServer server;

    private OpenAiClient client;

    private OpenAiClientSse sseClient;
    @Fit
    private Config config;
    @Fit
    private HttpClassicClientFactory httpClientFactory;
    @Mock
    private Decryptor decryptor;
    @Fit
    private BeanContainer container;

    @BeforeEach
    public void setUp() {
        this.server = new MockWebServer();
        this.client = new OpenAiClient("http://localhost:" + this.server.getPort(), null);
        this.sseClient = new OpenAiClientSse("http://localhost:" + this.server.getPort(),
                this.config,
                this.httpClientFactory, SERIALIZER, this.container);
        Mockito.doAnswer((Answer<Void>) invocation -> invocation.getArgument(0))
                .when(this.decryptor)
                .decrypt(any(String.class));
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
        this.server.enqueue(new MockResponse().setBody(this.getMockStreamResponseBody(contents))
                .setHeader("content-type", "text/event-stream"));

        OpenAiChatModelStreamService service = new OpenAiChatModelStreamService(this.sseClient);
        Choir<FlatChatMessage> choir = service.generate(this.getRequest());

        List<FlatChatMessage> response = choir.blockAll();
        assertThat(response).extracting(FlatChatMessage::getText).isEqualTo(contents);
    }

    @Test
    public void shouldNotContainNullItemWhenSerializerObj() {
        OpenAiChatCompletionRequest request = OpenAiChatCompletionRequest.builder().messages(Collections.singletonList(
                OpenAiChatMessage.builder().role("ai").build())).model("model").build();
        String serialize = SERIALIZER.serialize(request);

        assertThat(serialize).isEqualTo("{\"messages\":[{\"role\":\"ai\"}],\"model\":\"model\",\"stream\":false}");
    }

    @Test
    public void testClientWithConfig() {
        Map<String, Object> configMap = new HashMap<>();
        this.client = new OpenAiClient("http://localhost:" + this.server.getPort(), new ReadonlyMapConfig(configMap));
        testOpenAiChatModelService();
        testOpenAiEmbedModelService();
    }

    private ChatCompletion getRequest() {
        FlatChatMessage msg = FlatChatMessage.from(new HumanMessage("test"));

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
        return "{\"id\": \"0\"," + "\"object\": \"chat.completion\"," + "\"created\": 0," + "\"model\": \"test_model\","
                + "\"choices\": [{\"index\": 0," + "\"message\": {\"role\": \"assistant\",\"content\": \"" + content
                + "\"}," + "\"finish_reason\": \"length\"}],"
                + "\"usage\": {\"prompt_tokens\": 0,\"completion_tokens\": 0,\"total_tokens\": 0}}";
    }

    private String getMockEmbeddingResponseBody(List<Float> vector) {
        String embeddings = vector.stream().map(String::valueOf).collect(Collectors.joining(","));
        return "{\"object\": \"list\"," + "\"data\": [{\"index\": 0," + "\"object\": \"embedding\",\"embedding\": ["
                + embeddings + "]}]," + "\"model\": \"bce-embedding-base\","
                + "\"usage\": {\"prompt_tokens\": 0,\"total_tokens\": 0}}";
    }

    private String getMockStreamResponseBody(List<String> contents) {
        StringBuilder sb = new StringBuilder();
        contents.forEach(content -> sb.append(this.getMockStreamResponseChunk(content)));
        sb.append("data: [DONE]\n");
        return sb.toString();
    }

    private String getMockStreamResponseChunk(String content) {
        return "data: {\"id\": \"0\"," + "\"object\": \"chat.completion.chunk\"," + "\"created\": 0,"
                + "\"model\": \"test_model\"," + "\"choices\": [{\"index\": 0,\"delta\": {\"content\": \"" + content
                + "\"}," + "\"finish_reason\": null}]}\n\n";
    }
}
