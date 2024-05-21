/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 用于解析大模型请求、响应中有关统计信息部分，目前按照OpenAI API格式定义。
 *
 * @author 张庭怿
 * @since 2024-05-20
 */
@Data
public class ModelStatistics {
    private String model;

    private Usage usage;

    /**
     * 模型用量。
     */
    @Data
    public static class Usage {
        @JsonProperty(value = "prompt_tokens")
        private int promptTokens;

        @JsonProperty(value = "completion_tokens")
        private int completionTokens;

        @JsonProperty(value = "total_tokens")
        private int totalTokens;
    }
}
