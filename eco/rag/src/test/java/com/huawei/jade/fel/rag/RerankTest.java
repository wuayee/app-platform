/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.huawei.jade.fel.chat.ChatModelService;
import com.huawei.jade.fel.chat.character.AiMessage;
import com.huawei.jade.fel.chat.protocol.ChatCompletion;
import com.huawei.jade.fel.chat.protocol.FlatChatMessage;
import com.huawei.jade.fel.rag.rerank.LlmRerank;
import com.huawei.jade.fel.rag.rerank.RrfRerank;
import com.huawei.jade.fel.rag.rerank.WeightedRerank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * rerank模块测试。
 *
 * @since 2024-05-08
 */
public class RerankTest {
    private static final String MODEL_OUTPUT = "[3, 2, 1]";

    private Chunks chunks;
    private Chunks chunks2;

    @BeforeEach
    void init() {
        chunks = Chunks.from(Arrays.asList(
                new Chunk("1", "content1", null).addMetadata("score", 0.8),
                new Chunk("2", "content2", null).addMetadata("score", 0.1),
                new Chunk("3", "content3", null).addMetadata("score", 0.1)
        ));

        chunks2 = Chunks.from(Arrays.asList(
                new Chunk("1", "content1", null).addMetadata("score", 0.6),
                new Chunk("4", "content4", null).addMetadata("score", 0.2),
                new Chunk("2", "content2", null).addMetadata("score", 0.2)
        ));
    }

    private class MockModel implements ChatModelService {
        @Override
        public FlatChatMessage generate(ChatCompletion chatCompletion) {
            return FlatChatMessage.from(new AiMessage(MODEL_OUTPUT));
        }
    }

    @Test
    void test_llm_rerank() {
        MockModel mockModel = new MockModel();
        LlmRerank rerank = new LlmRerank(mockModel);
        assertEquals(String.join(System.lineSeparator(), "content3", "content2", "content1"),
                rerank.invoke("query", chunks).text());
    }

    @Test
    void test_rrf_rerank() {
        RrfRerank rerank = new RrfRerank();
        assertEquals(String.join(System.lineSeparator(), "content1", "content2", "content4"),
                rerank.invoke(Arrays.asList(chunks, chunks2)).text());
    }

    @Test
    void test_weighted_rerank() {
        WeightedRerank rerank = new WeightedRerank();
        assertEquals(String.join(System.lineSeparator(), "content1", "content2", "content4"),
                rerank.invoke(Arrays.asList(chunks, chunks2)).text());
    }
}
