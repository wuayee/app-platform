/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.model.openai.entity.embed;

import lombok.Data;

import java.util.List;

/**
 * OpenAi API Embedding 响应
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024-4-30
 */
@Data
public class OpenAiEmbedding {
    /**
     * @see <a href="https://platform.openai.com/docs/api-reference/embeddings/object#embeddings/object-embedding">
     * OpenAI API</a>
     */
    private List<Float> embedding;
}
