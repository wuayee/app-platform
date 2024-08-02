/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.http.entity.TextEntity;
import com.huawei.fit.jober.aipp.enums.LlmModelNameEnum;
import com.huawei.fit.jober.aipp.service.impl.LLMServiceImpl;
import com.huawei.jade.fel.model.openai.client.OpenAiClient;
import com.huawei.jade.fel.model.openai.entity.chat.OpenAiChatCompletionResponse;
import com.huawei.jade.voice.service.VoiceService;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * LlmService测试类
 *
 * @since 2024-07-31
 */
public class LlmServiceTest {
    private static final OpenAiClient OPENAI_CLIENT_MOCK = mock(OpenAiClient.class);
    private static final VoiceService VOICE_SERVICE_MOCK = mock(VoiceService.class);
    private static final HttpClassicClientFactory FACTORY_MOCK =
            mock(HttpClassicClientFactory.class, RETURNS_DEEP_STUBS);

    @Test
    void testAskXiaohaiKnowledge() {
        LLMService llmServiceMock = new LLMServiceImpl(
                null, null, 1000,
                null, null, OPENAI_CLIENT_MOCK, VOICE_SERVICE_MOCK, FACTORY_MOCK);
        HttpClassicClientRequest requestMock = mock(HttpClassicClientRequest.class);
        HttpClassicClientResponse<Object> responseMock = mock(HttpClassicClientResponse.class);
        TextEntity textEntityMock = mock(TextEntity.class);
        when(requestMock.exchange()).thenReturn(responseMock);
        when(responseMock.statusCode()).thenReturn(200);
        when(responseMock.textEntity()).thenReturn(Optional.of(textEntityMock));
        when(textEntityMock.content()).thenReturn("{\"msg\":\"test\"}");
        when(FACTORY_MOCK.create(any()).createRequest(any(), any())).thenReturn(requestMock);
        try {
            String res = llmServiceMock.askXiaoHaiKnowledge(null, null);
            assertEquals("test", res);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testAskWithImage() {
        LLMService llmServiceMock = new LLMServiceImpl(
                null, null, 1000,
                null, null, OPENAI_CLIENT_MOCK, VOICE_SERVICE_MOCK, FACTORY_MOCK);
        OpenAiChatCompletionResponse responseMock = mock(OpenAiChatCompletionResponse.class, RETURNS_DEEP_STUBS);
        File imageMock = null;
        try {
            imageMock = Paths.get(System.getProperty("java.io.tmpdir"), "test.png").toFile();
            when(OPENAI_CLIENT_MOCK.createChatCompletion(any())).thenReturn(responseMock);
            when(responseMock.getChoices().get(0).getMessage().getContent()).thenReturn("image test");
            String res = llmServiceMock.askModelWithImage(imageMock, "请介绍一下图像内容");
            assertEquals("image test", res);
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            if (imageMock != null) {
                imageMock.deleteOnExit();
            }
        }
    }

    @Test
    void testAskWithAudio() {
        LLMService llmServiceMock = new LLMServiceImpl(
                null, null, 1000,
                null, null, OPENAI_CLIENT_MOCK, VOICE_SERVICE_MOCK, FACTORY_MOCK);
        File audioMock = null;
        try {
            audioMock = Paths.get(System.getProperty("java.io.tmpdir"), "test.mp3").toFile();
            when(VOICE_SERVICE_MOCK.getText(any(), any())).thenReturn("测试");
            String res = llmServiceMock.askModelWithAudio(audioMock);
            assertEquals("测试", res);
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            if (audioMock != null) {
                audioMock.deleteOnExit();
            }
        }
    }

    @Test
    void testAskWithText() {
        LLMService llmServiceMock = new LLMServiceImpl(
                null, null, 1000,
                null, null, OPENAI_CLIENT_MOCK, VOICE_SERVICE_MOCK, FACTORY_MOCK);
        OpenAiChatCompletionResponse responseMock = mock(OpenAiChatCompletionResponse.class, RETURNS_DEEP_STUBS);
        try {
            when(OPENAI_CLIENT_MOCK.createChatCompletion(any())).thenReturn(responseMock);
            when(responseMock.getChoices().get(0).getMessage().getContent()).thenReturn("测试回复");
            String res = llmServiceMock.askModelWithText("测试1", LlmModelNameEnum.QWEN_72B);
            assertEquals("测试回复", res);
            res = llmServiceMock.askModelWithText("测试2", 16000, 0.2, LlmModelNameEnum.QWEN_72B);
            assertEquals("测试回复", res);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
