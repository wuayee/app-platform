/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.model.openai.entity.chat;

import com.huawei.jade.fel.model.openai.entity.chat.message.OpenAiChatMessage;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

/**
 * 模型响应中的回答选择，每个选择中包含一条消息。
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024-4-30
 */
@Data
public class OpenAiChatCompletionChoice {
    /**
     * 表示一条模型消息。
     */
    @JsonAlias("delta")
    private OpenAiChatMessage message;
}
