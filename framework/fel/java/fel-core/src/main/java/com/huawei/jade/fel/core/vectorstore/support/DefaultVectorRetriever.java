/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.vectorstore.support;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.fel.core.document.MeasurableDocument;
import com.huawei.jade.fel.core.pattern.Retriever;
import com.huawei.jade.fel.core.vectorstore.SearchOption;
import com.huawei.jade.fel.core.vectorstore.VectorStore;

import java.util.List;

/**
 * 表示默认的向量检索器。
 *
 * @author 易文渊
 * @since 2024-08-06
 */
public class DefaultVectorRetriever implements Retriever<String, MeasurableDocument> {
    private final VectorStore vectorStore;
    private final SearchOption searchOption;

    /**
     * 构造默认的向量检索器。
     *
     * @param vectorStore 表示向量数据库的 {@link VectorStore}。
     * @param searchOption 表示查询参数的 {@link SearchOption}。
     */
    public DefaultVectorRetriever(VectorStore vectorStore, SearchOption searchOption) {
        this.vectorStore = Validation.notNull(vectorStore, "The vector store cannot be null.");
        this.searchOption = Validation.notNull(searchOption, "The search option cannot be null.");
    }

    @Override
    public List<MeasurableDocument> retrieve(String query) {
        return this.vectorStore.search(query, searchOption);
    }
}