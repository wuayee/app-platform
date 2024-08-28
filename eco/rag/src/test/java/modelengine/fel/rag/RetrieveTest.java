/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import modelengine.fel.chat.ChatModelService;
import modelengine.fel.chat.character.AiMessage;
import modelengine.fel.chat.protocol.ChatCompletion;
import modelengine.fel.chat.protocol.FlatChatMessage;
import modelengine.fel.rag.index.VectorIndex;
import modelengine.fel.rag.rerank.LlmRerank;
import modelengine.fel.rag.rerank.RrfRerank;
import modelengine.fel.rag.retrieve.BasicRetriever;
import modelengine.fel.rag.retrieve.HybridRerankRetriever;
import modelengine.fel.rag.retrieve.ModelRerankRetriever;
import modelengine.fel.rag.store.query.Expression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * retriever模块测试。
 *
 * @since 2024-05-08
 */
public class RetrieveTest {
    private static final String MODEL_OUTPUT = "[3, 2, 1]";

    private List<Chunk> chunks;
    private List<Chunk> chunks2;

    private class MockVectorIndexer extends VectorIndex {
        private List<Chunk> chunks;

        /**
         * MockVectorIndexer
         *
         * @param chunks chunks
         */
        public MockVectorIndexer(List<Chunk> chunks) {
            super(null, null);
            this.chunks = chunks;
        }

        @Override
        public List<Chunk> searchChunks(String queryStr, int topK, Expression expr) {
            return this.chunks;
        }
    }

    private class MockModel implements ChatModelService {
        @Override
        public FlatChatMessage generate(ChatCompletion chatCompletion) {
            return FlatChatMessage.from(new AiMessage(MODEL_OUTPUT));
        }
    }

    @BeforeEach
    private void init() {
        chunks = Arrays.asList(
                new Chunk("1", "content1", null).addMetadata("score", 0.8),
                new Chunk("2", "content2", null).addMetadata("score", 0.1),
                new Chunk("3", "content3", null).addMetadata("score", 0.1)
        );

        chunks2 = Arrays.asList(
                new Chunk("1", "content1", null).addMetadata("score", 0.6),
                new Chunk("4", "content4", null).addMetadata("score", 0.2),
                new Chunk("2", "content2", null).addMetadata("score", 0.2)
        );
    }

    @Test
    void test_basic_retrieve() {
        BasicRetriever retriever = new BasicRetriever(new MockVectorIndexer(chunks), 2);
        assertEquals(String.join(System.lineSeparator(), "content1", "content2"),
                retriever.invoke("question"));
    }

    @Test
    void test_hybrid_retrieve() {
        HybridRerankRetriever retriever = new HybridRerankRetriever(
                Arrays.asList(new MockVectorIndexer(chunks), new MockVectorIndexer(chunks2)),
                new RrfRerank(),
                2
        );
        assertEquals(String.join(System.lineSeparator(), "content1", "content2"),
                retriever.invoke("question").text());
    }

    @Test
    void test_model_retrieve() {
        ModelRerankRetriever retriever = new ModelRerankRetriever(
                new MockVectorIndexer(chunks),
                new LlmRerank(new MockModel()),
                2
        );
        assertEquals(String.join(System.lineSeparator(), "content3", "content2"),
                retriever.invoke("question"));
    }
}
