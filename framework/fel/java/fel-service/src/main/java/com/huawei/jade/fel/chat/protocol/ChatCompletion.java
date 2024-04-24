/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.chat.protocol;

import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.ChatOptions;
import com.huawei.jade.fel.chat.Prompt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示聊天模型请求实体类。
 *
 * @author 易文渊
 * @since 2024-04-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletion implements Prompt {
    /**
     * 会话消息列表。
     */
    private List<FlatChatMessage> messages;

    /**
     * 模型参数。
     */
    private ChatOptions options;

    @Override
    public List<ChatMessage> messages() {
        return new ArrayList<>(messages);
    }

    @Override
    public ChatOptions option() {
        return options;
    }
}