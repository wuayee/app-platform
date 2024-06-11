/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.plugins.vectorindex;

import com.huawei.fit.jade.MilvusVectorConnector;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.fel.embed.EmbedModelService;
import com.huawei.jade.fel.embed.EmbedOptions;
import com.huawei.jade.fel.embed.EmbedRequest;
import com.huawei.jade.fel.embed.EmbedResponse;
import com.huawei.jade.fel.rag.index.IndexConfig;
import com.huawei.jade.fel.rag.index.IndexService;
import com.huawei.jade.fel.rag.index.IndexerOptions;
import com.huawei.jade.fel.rag.index.VectorIndex;
import com.huawei.jade.fel.rag.protocol.FlatChunk;
import com.huawei.jade.fel.rag.store.ChunkVectorStore;
import com.huawei.jade.fel.rag.store.VectorStore;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 向量型索引服务的实现。
 *
 * @since 2024-06-03
 */
@Component("vector-index-service")
public class VectorIndexService implements IndexService {
    private final EmbedModelService embedModelService;
    private final Map<String, VectorIndex> vectorIndexMap = new ConcurrentHashMap<>();

    /**
     * 根据传入的嵌入模型服务构造{@link VectorIndexService}实例。
     *
     * @param embedModelService 表示嵌入模型服务的{@link EmbedModelService}.
     */
    public VectorIndexService(EmbedModelService embedModelService) {
        this.embedModelService = Validation.notNull(embedModelService, "Embed model service cannot be null.");
    }


    @Override
    @Fitable("vector-index")
    public void index(List<FlatChunk> flatChunks, IndexerOptions options) {
        if (!this.vectorIndexMap.containsKey(options.getConnectorId())) {
            throw new IllegalArgumentException("Vector connector dosenot exists");
        }
        this.vectorIndexMap.get(options.getConnectorId())
                .process(flatChunks.stream().map(FlatChunk::toChunk).collect(Collectors.toList()));
    }

    @Override
    @Fitable("vector-index-search")
    public List<FlatChunk> search(String query, IndexerOptions options) {
        if (!this.vectorIndexMap.containsKey(options.getConnectorId())) {
            throw new IllegalArgumentException("Vector connector dosenot exists");
        }
        return this.vectorIndexMap.get(options.getConnectorId()).searchChunks(query, options.getTopK(), null)
                .stream().map(FlatChunk::new).collect(Collectors.toList());
    }

    @Override
    @Fitable("vector-add-connector")
    public String addConnector(IndexConfig config) {
        if (config == null || !config.isInvalid()) {
            throw new IllegalArgumentException("Vector index config invalid");
        }

        String connectorId = config.generateId();
        VectorIndex vectorIndex = this.vectorIndexMap.get(connectorId);
        if (vectorIndex == null) {
            VectorStore vectorStore = new ChunkVectorStore(new MilvusVectorConnector(config.getHost(), config.getPort(),
                    config.getUsername(), config.getPassword(), config.getDatabaseName()));
            this.vectorIndexMap.put(connectorId,
                    new VectorIndex(vectorStore, input -> {
                        EmbedOptions modelOptions = new EmbedOptions();
                        modelOptions.setModel(config.getEmbeddingModelName());

                        EmbedRequest request = new EmbedRequest();
                        request.setOptions(modelOptions);
                        request.setInputs(Collections.singletonList(input));

                        EmbedResponse generate = embedModelService.generate(request);

                        return generate.getEmbeddings().get(0);
                    }));
        }
        return connectorId;
    }

    @Override
    @Fitable("vector-remove-connector")
    public void removeConnector(String id) {
        this.vectorIndexMap.remove(id);
    }
}
