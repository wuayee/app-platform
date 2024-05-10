/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fel;

import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.character.AiMessage;
import com.huawei.jade.fel.chat.character.HumanMessage;
import com.huawei.jade.fel.core.memory.Memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * AippMemory
 *
 * @author 易文渊
 * @since 2024-04-23
 */
public class AippMemory implements Memory {
    private final List<ChatMessage> messages = new ArrayList<>();

    public AippMemory(List<Map<String, String>> data) {
        data.forEach(m -> {
            if (m.containsKey("question") && m.containsKey("answer")) {
                messages.add(new HumanMessage(m.get("question")));
                messages.add(new AiMessage(m.get("answer")));
            }
        });
    }

    @Override
    public void add(ChatMessage question, ChatMessage answer) {
    }

    @Override
    public List<ChatMessage> messages() {
        return messages;
    }

    @Override
    public String text() {
        return null;
    }
}