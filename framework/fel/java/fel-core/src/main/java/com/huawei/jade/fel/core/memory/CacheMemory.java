/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.memory;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.fel.chat.ChatMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 *  {@link Memory} 的简单内存实现。
 *
 * @author 刘信宏
 * @since 2024-05-08
 */
public class CacheMemory implements Memory {
    private static final String COLON = ":";
    private static final String BREAK = "\n";

    private final List<ChatMessage> messages = new ArrayList<>();

    @Override
    public void add(ChatMessage question, ChatMessage answer) {
        this.messages.add(Validation.notNull(question, "Question cannot be null."));
        this.messages.add(Validation.notNull(answer, "Answer cannot be null."));
    }

    @Override
    public List<ChatMessage> messages() {
        return Collections.unmodifiableList(this.messages);
    }

    @Override
    public String text() {
        StringBuilder builder = new StringBuilder();
        String breakStr = StringUtils.EMPTY;
        for (ChatMessage message : this.messages) {
            builder.append(breakStr)
                    .append(message.type().toString().toLowerCase(Locale.ROOT))
                    .append(COLON)
                    .append(message.text());
            breakStr = BREAK;
        }
        return builder.toString();
    }
}
