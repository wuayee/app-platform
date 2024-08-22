/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.plugins.llmrerank;

import static org.junit.jupiter.api.Assertions.assertEquals;

import modelengine.fel.chat.ChatModelService;
import modelengine.fel.chat.character.AiMessage;
import modelengine.fel.chat.protocol.ChatCompletion;
import modelengine.fel.chat.protocol.FlatChatMessage;
import modelengine.fel.rag.Chunk;
import modelengine.fel.rag.protocol.FlatChunk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LlmRerankService模块测试。
 *
 * @since 2024-06-03
 */
public class LlmRerankServiceTest {
    private static final String MODEL_OUTPUT = "[3, 2, 1]";

    private List<FlatChunk> flatChunks;

    @BeforeEach
    void init() {
        flatChunks = Arrays.asList(
                new FlatChunk(new Chunk("1", "content1", null).addMetadata("score", 0.8)),
                new FlatChunk(new Chunk("2", "content2", null).addMetadata("score", 0.1)),
                new FlatChunk(new Chunk("3", "content3", null).addMetadata("score", 0.1))
        );
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
        LlmRerankService service = new LlmRerankService(mockModel);
        assertEquals(String.join(System.lineSeparator(), "content3", "content2", "content1"),
                service.rerank("query", flatChunks).stream()
                        .map(FlatChunk::getContent)
                        .collect(Collectors.joining(System.lineSeparator())));
    }
}