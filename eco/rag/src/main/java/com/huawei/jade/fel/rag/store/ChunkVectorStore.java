/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.store;

import com.huawei.jade.fel.rag.common.Chunk;
import com.huawei.jade.fel.rag.common.RequireType;
import com.huawei.jade.fel.rag.store.config.IndexType;
import com.huawei.jade.fel.rag.store.config.MetricType;
import com.huawei.jade.fel.rag.store.config.VectorConfig;
import com.huawei.jade.fel.rag.store.connector.VectorConnector;
import com.huawei.jade.fel.rag.store.connector.schema.VectorFieldDataType;
import com.huawei.jade.fel.rag.store.connector.schema.VectorSchema;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;

/**
 * 处理Chunk的向量数据库。
 *
 * @since 2024-05-07
 */
public class ChunkVectorStore extends VectorStore<List<Chunk>, List<Chunk>> {
    private static final String ID = "id";
    private static final String EMBEDDING = "embedding";
    private static final String CONTENT = "content";
    private static final String METADATA = "metadata";
    private static final String CONFIG_FILE = "conifg.yml";


    private Gson gson;

    /**
     * 根据传入的向量数据库连接器构建 {@link ChunkVectorStore} 实例。
     *
     * @param conn 表示向量数据库连接器的 {@link VectorConnector}。
     */
    public ChunkVectorStore(VectorConnector conn) {
        super(conn);
        VectorSchema schema = new VectorSchema();
        schema.addField(ID, VectorFieldDataType.VARCHAR, Arrays.asList("isPrimaryKey", true, "maxLen", 36));
        schema.addField(CONTENT, VectorFieldDataType.VARCHAR, Arrays.asList("maxLen", 65535));
        schema.addField(EMBEDDING, VectorFieldDataType.FLOATVECTOR, Arrays.asList("dimension", 768));
        schema.addField(METADATA, VectorFieldDataType.VARCHAR, Arrays.asList("maxLen", 65535));

        VectorConfig config = VectorConfig.builder()
                .databaseName("default")
                .collectionName("vectorStore")
                .vectorFieldName(EMBEDDING)
                .indexType(IndexType.IVF_FLAT)
                .metricType(MetricType.L2)
                .extraParam("{\"nlist\":16384}")
                .schema(schema)
                .build();

        setConfig(config);
        gson = new Gson();
    }

    @Override
    protected List<Map<String, Object>> formatInput(List<Chunk> input) {
        List<Map<String, Object>> res = new ArrayList<>();
        input.forEach((chunk) -> res.add(formatChunk(chunk)));
        return res;
    }

    @Override
    protected List<Chunk> parseOutput(List<Pair<Map<String, Object>, Float>> value) {
        List<Chunk> res = new ArrayList<>();
        value.forEach((p) -> res.add(parseChunk(p)));
        return res;
    }

    private Map<String, Object> formatChunk(Chunk chunk) {
        return new HashMap<String, Object>() {
            {
                put(ID, chunk.getId());
                put(EMBEDDING, chunk.getMetadata().get(EMBEDDING));
                put(CONTENT, chunk.getContent());
                Map<String, Object> meta = chunk.getMetadata();
                meta.remove(EMBEDDING);
                put(METADATA, gson.toJson(meta));
            }
        };
    }

    private Chunk parseChunk(Pair<Map<String, Object>, Float> input) {
        return new Chunk(
                RequireType.check(input.getKey().get(this.ID), String.class),
                RequireType.check(input.getKey().get(this.CONTENT), String.class),
                gson.fromJson(RequireType.check(input.getKey().get(this.METADATA), String.class), HashMap.class))
                .addMetadata("score", Double.valueOf(input.getValue()));
    }
}
