/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.split;

import modelengine.fitframework.annotation.Genericable;
import modelengine.fel.rag.protocol.FlatChunk;
import modelengine.fel.rag.protocol.FlatDocument;

import java.util.List;

/**
 * Token切分器服务。
 *
 * @since 2024-06-03
 */
public interface DocumentSplitterService {
    /**
     * 将文档进行切分。
     *
     * @param input 表示检索问题的 {@link List}{@code <}{@link FlatDocument}{@code >}。
     * @param options 表示检索服务超参数的 {@link SplitOptions}。
     * @return 表示切分后内容的 {@link List}{@code <}{@link FlatChunk}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.fel.rag.split.tokensplit")
    List<FlatChunk> split(List<FlatDocument> input, SplitOptions options);
}
