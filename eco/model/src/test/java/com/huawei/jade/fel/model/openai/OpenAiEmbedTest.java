/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.model.openai;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.huawei.jade.fel.model.openai.entity.embed.OpenAiEmbedding;
import com.huawei.jade.fel.model.openai.entity.embed.OpenAiEmbeddingRequest;
import com.huawei.jade.fel.model.openai.entity.embed.OpenAiEmbeddingResponse;
import com.huawei.jade.fel.model.openai.utils.OpenAiMessageUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * OpenAI Embedding 相关对象的序列化与反序列化单元测试。
 *
 * @author 张庭怿
 * @since 2024-4-30
 */
public class OpenAiEmbedTest {
    @Test
    void testOpenAiEmbeddingSerialization() throws JsonProcessingException {
        OpenAiEmbedding embedding = new OpenAiEmbedding();
        embedding.setEmbedding(Arrays.asList(0.1F, 0.2F, 0.3F));
        String json = "{\"embedding\":[0.1,0.2,0.3]}";
        assertEquals(json, OpenAiMessageUtils.OBJECT_MAPPER.writeValueAsString(embedding));
    }

    @Test
    void testOpenAiEmbeddingRequestSerialization() throws JsonProcessingException {
        OpenAiEmbeddingRequest request = OpenAiEmbeddingRequest.builder()
                .model("test_model")
                .input(Collections.singletonList("test"))
                .build();
        String json = "{\"model\":\"test_model\",\"input\":[\"test\"]}";
        assertEquals(json, OpenAiMessageUtils.OBJECT_MAPPER.writeValueAsString(request));
    }

    @Test
    void testOpenAiEmbeddingResponseDeserialization() throws JsonProcessingException {
        String json = "{\"object\":\"list\","
                + "\"data\":[{\"index\":0,\"object\":\"embedding\",\"embedding\":[0.1,0.2,0.3]}],"
                + "\"usage\":{\"prompt_tokens\":1,\"total_tokens\":2}}";
        OpenAiEmbeddingResponse response =
                OpenAiMessageUtils.OBJECT_MAPPER.readValue(json, OpenAiEmbeddingResponse.class);
        assertThat(response)
                .extracting(r -> r.getData().get(0).getEmbedding(),
                        r -> r.getUsage().getPromptTokens(),
                        r -> r.getUsage().getTotalTokens())
                .containsExactly(Arrays.asList(0.1F, 0.2F, 0.3F), 1, 2);
    }
}
