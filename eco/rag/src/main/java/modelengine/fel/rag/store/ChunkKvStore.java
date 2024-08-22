/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.store;

import modelengine.fel.rag.Chunk;
import modelengine.fel.rag.store.config.KvConfig;
import modelengine.fel.rag.store.connector.KvConnector;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理Chunk的键值数据库。
 *
 * @since 2024-05-07
 */
public class ChunkKvStore extends KvStore<List<Chunk>, Chunk> {
    private Gson gson;

    /**
     * 根据传入的键值型数据库连接器构建 {@link ChunkKvStore} 实例。
     *
     * @param conn 表示键值型数据库连接器的 {@link KvConnector}。
     * @param namespace 表示namespace的 {@link String}。
     */
    public ChunkKvStore(KvConnector conn, String namespace) {
        super(conn);
        setConfig(new KvConfig(namespace));
        gson = new Gson();
    }

    @Override
    protected Map<String, String> formatInput(List<Chunk> input) {
        Map<String, String> res = new HashMap<>();
        input.forEach((chunk) -> {
            res.put(chunk.getId(), gson.toJson(chunk));
        });
        return res;
    }

    @Override
    protected Chunk parseOutput(String value) {
        return gson.fromJson(value, Chunk.class);
    }
}
