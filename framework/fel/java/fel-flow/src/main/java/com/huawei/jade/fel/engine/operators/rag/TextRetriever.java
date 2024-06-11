/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.rag;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.fel.core.retriever.Retriever;
import com.huawei.jade.fel.rag.protocol.FlatChunk;
import com.huawei.jade.fel.rag.retriever.RetrieverOptions;
import com.huawei.jade.fel.rag.retriever.TextRetrieverService;

import java.util.List;

/**
 * 文本检索器。
 *
 * @author 刘信宏
 * @since 2024-05-31
 */
public class TextRetriever implements Retriever<String, List<FlatChunk>> {
    private final TextRetrieverService textRetrieverService;
    private final RetrieverOptions options;

    public TextRetriever(TextRetrieverService retrieverService, RetrieverOptions options) {
        this.textRetrieverService = Validation.notNull(retrieverService, "Retriever service can not be null.");
        this.options = ObjectUtils.nullIf(options, new RetrieverOptions());
    }

    @Override
    public List<FlatChunk> invoke(String input) {
        return textRetrieverService.retrieve(input, this.options);
    }
}
