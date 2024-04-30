/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.model.openai.entity.embed;

import com.huawei.jade.fel.model.openai.entity.Usage;

import lombok.Data;

import java.util.List;

/**
 * OpenAi API 格式的 Embedding 响应。
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024-4-30
 */
@Data
public class OpenAiEmbeddingResponse {
    /**
     * 模型生成的 embeddings 列表。
     */
    private List<OpenAiEmbedding> data;

    /**
     * 模型使用量统计信息 {@link Usage} 。
     */
    private Usage usage;
}
