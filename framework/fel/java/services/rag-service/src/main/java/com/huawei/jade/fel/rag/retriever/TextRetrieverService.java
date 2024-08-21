/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.retriever;

import modelengine.fitframework.annotation.Genericable;
import com.huawei.jade.fel.rag.protocol.FlatChunk;

import java.util.List;

/**
 * 表示检索服务。
 *
 * @author 刘信宏
 * @since 2024-05-31
 */
public interface TextRetrieverService {
    /**
     * 检索文本。
     *
     * @param query 表示检索问题的 {@link String}。
     * @param options 表示检索服务超参数的 {@link RetrieverOptions}。
     * @return 表示检索返回内容的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.fel.rag.retriever.text")
    List<FlatChunk> retrieve(String query, RetrieverOptions options);
}
