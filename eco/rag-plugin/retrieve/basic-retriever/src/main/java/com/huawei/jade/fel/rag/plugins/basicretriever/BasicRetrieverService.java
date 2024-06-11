/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.plugins.basicretriever;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.fel.rag.index.IndexService;
import com.huawei.jade.fel.rag.index.IndexerOptions;
import com.huawei.jade.fel.rag.protocol.FlatChunk;
import com.huawei.jade.fel.rag.retriever.RetrieverOptions;
import com.huawei.jade.fel.rag.retriever.TextRetrieverService;

import java.util.List;

/**
 * 简单检索服务实现。
 *
 * @since 2024-06-03
 */
@Component
public class BasicRetrieverService implements TextRetrieverService {
    private final IndexService indexService;

    /**
     * 根据传入的向量索引服务构造{@link BasicRetrieverService}的实例。
     *
     * @param indexService 表示向量索引服务的{@link IndexService}.
     */
    public BasicRetrieverService(@Fit(alias = "vector-index-service") IndexService indexService) {
        this.indexService = Validation.notNull(indexService, "Index service cannot be null.");
    }

    @Override
    @Fitable("basic-retriever")
    public List<FlatChunk> retrieve(String query, RetrieverOptions options) {
        return this.indexService.search(query, IndexerOptions.builder().topK(options.getTopK()).expr("").build());
    }
}
