/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.rerank;

import modelengine.fel.rag.protocol.FlatChunk;
import modelengine.fitframework.annotation.Genericable;

import java.util.List;

/**
 * 模型重排序服务
 *
 * @since 2024-06-03
 */
public interface ModelRerankService {
    /**
     * 利用模型将传入的数据列表根据查询进行重排序.
     *
     * @param query 表示查询的 {@link String}。
     * @param data 表示输入数据列表的 {@link List} {@code <}{@link FlatChunk}{@code >}。
     * @return 返回排序后的数据列表。
     */
    @Genericable(id = "modelengine.fel.rag.rerank.model")
    List<FlatChunk> rerank(String query, List<FlatChunk> data);
}
