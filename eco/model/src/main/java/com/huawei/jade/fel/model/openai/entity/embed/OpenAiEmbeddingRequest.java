/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.model.openai.entity.embed;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

/**
 * OpenAi API 格式的 Embedding 请求。
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024-4-30
 */
@Builder
@Data
public class OpenAiEmbeddingRequest {
    /**
     * @see <a href="https://platform.openai.com/docs/api-reference/embeddings/create#embeddings-create-model">
     * OpenAI API</a>
     */
    @NonNull
    private String model;

    /**
     * @see <a href="https://platform.openai.com/docs/api-reference/embeddings/create#embeddings-create-input">
     * OpenAI API</a>
     */
    @NonNull
    private List<String> input;
}
