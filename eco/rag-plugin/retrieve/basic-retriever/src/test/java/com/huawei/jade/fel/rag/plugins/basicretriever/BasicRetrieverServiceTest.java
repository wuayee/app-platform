/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.plugins.basicretriever;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.huawei.jade.fel.rag.Chunk;
import com.huawei.jade.fel.rag.index.IndexConfig;
import com.huawei.jade.fel.rag.index.IndexService;
import com.huawei.jade.fel.rag.index.IndexerOptions;
import com.huawei.jade.fel.rag.protocol.FlatChunk;
import com.huawei.jade.fel.rag.retriever.RetrieverOptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 简单检索服务测试类。
 *
 * @since 2024-06-04
 */
public class BasicRetrieverServiceTest {
    private List<FlatChunk> chunks;

    private class MockVecIndexService implements IndexService {
        private List<FlatChunk> chunks;

        /**
         * 模拟向量索引服务。
         *
         * @param chunks chunks
         */
        public MockVecIndexService(List<FlatChunk> chunks) {
            this.chunks = chunks;
        }

        @Override
        public void index(List<FlatChunk> flatChunks, IndexerOptions options) {

        }

        @Override
        public List<FlatChunk> search(String query, IndexerOptions options) {
            return chunks.stream().limit(options.getTopK()).collect(Collectors.toList());
        }

        @Override
        public String addConnector(IndexConfig config) {
            return "";
        }

        @Override
        public void removeConnector(String id) {

        }
    }
    @BeforeEach
    private void init() {
        chunks = Arrays.asList(
                new FlatChunk(new Chunk("1", "content1", null).addMetadata("score", 0.8)),
                new FlatChunk(new Chunk("2", "content2", null).addMetadata("score", 0.1)),
                new FlatChunk(new Chunk("3", "content3", null).addMetadata("score", 0.1))
        );
    }

    @Test
    void test_basic_retrieve() {
        BasicRetrieverService service = new BasicRetrieverService(new MockVecIndexService(chunks));
        RetrieverOptions options = new RetrieverOptions();
        options.setTopK(2);
        assertEquals(String.join(System.lineSeparator(), "content1", "content2"),
                service.retrieve("query", options).stream()
                        .map(FlatChunk::getContent)
                        .collect(Collectors.joining(System.lineSeparator())));
    }
}
