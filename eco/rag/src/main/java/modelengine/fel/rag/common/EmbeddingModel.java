/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.common;

import java.util.List;

/**
 * 向量化模型接口。
 *
 * @since 2024-05-07
 */
public interface EmbeddingModel {
    /**
     * 将传入的内容进行向量化并返回。
     * <p>任何向量化模型都必须实现该接口。</p>
     *
     * @param input 表示传入数据内容的 {@link String}。
     * @return 表示向量化后的数据。
     */
    List<Float> invoke(String input);
}