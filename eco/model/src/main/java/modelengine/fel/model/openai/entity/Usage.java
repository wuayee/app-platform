/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.model.openai.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 模型使用统计信息。
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024-4-30
 */
@Data
public class Usage {
    @JsonProperty("completion_tokens")
    private int completionTokens;

    @JsonProperty("prompt_tokens")
    private int promptTokens;

    @JsonProperty("total_tokens")
    private int totalTokens;
}
