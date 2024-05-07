/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.huawei.jade.fel.rag.common.EmbeddingModel;
import com.huawei.jade.fel.rag.index.VectorIndex;
import com.huawei.jade.fel.rag.store.ChunkVectorStore;
import com.huawei.jade.fel.rag.store.config.VectorConfig;
import com.huawei.jade.fel.rag.store.connector.VectorConnector;
import com.huawei.jade.fel.rag.store.query.Expression;
import com.huawei.jade.fel.rag.store.query.VectorQuery;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.util.Pair;

/**
 * VectorIndex模块测试。
 *
 * @since 2024-05-09
 */
public class VectorIndexTest {
    private class MockVecConn implements VectorConnector {
        @Override
        public List<Pair<Map<String, Object>, Float>> get(VectorQuery query, VectorConfig conf) {
            Map<String, Object> mp = new HashMap<>();
            mp.put("id", "1");
            mp.put("content", "content1");
            mp.put("metadata", "{\"sourceId\":\"0\"}");
            return Arrays.asList(new Pair<>(mp, 0.9f));
        }

        @Override
        public void put(List<Map<String, Object>> records, VectorConfig conf) {
        }

        @Override
        public Boolean delete(Expression expr, VectorConfig conf) {
            return true;
        }

        @Override
        public void createCollection(VectorConfig conf) {
        }

        @Override
        public void dropCollection(VectorConfig conf) {
        }

        @Override
        public void close() {
        }
    }

    private class MockEmbedding implements EmbeddingModel {
        @Override
        public List<Float> invoke(String input) {
            List<Double> embedding = Arrays.asList(1.0, 2.0, 3.0);
            return embedding.stream()
                    .map(Double::floatValue)
                    .collect(Collectors.toList());
        }
    }

    @Test
    void test_vector_index() {
        VectorIndex vi = new VectorIndex(new ChunkVectorStore(new MockVecConn()), new MockEmbedding());
        assertEquals("content1", vi.searchChunks("question", 1, null).get(0).getContent());
    }
}
