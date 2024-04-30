/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.model.openai.entity.chat;

import com.huawei.jade.fel.model.openai.entity.Usage;

import lombok.Data;

import java.util.List;

/**
 * OpenAi API 格式的会话补全响应。
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024-4-30
 */
@Data
public class OpenAiChatCompletionResponse {
    /**
     * @see <a href="https://platform.openai.com/docs/api-reference/chat/object#chat/object-choices">OpenAI API</a>
     */
    private List<OpenAiChatCompletionChoice> choices;

    /**
     * @see <a href="https://platform.openai.com/docs/api-reference/chat/object#chat/object-usage">OpenAI API</a>
     */
    private Usage usage;
}
