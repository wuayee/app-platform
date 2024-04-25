/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.chat.charactar;

import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.content.Contents;

/**
 * 表示聊天消息的抽象实现。
 *
 * @author 易文渊
 * @since 2024-04-16
 */
public abstract class AbstractChatMessage implements ChatMessage {
    /**
     * 表示聊天消息的内容。
     */
    protected final Contents contents;

    /**
     * 通过文本信息来初始化 {@link AbstractChatMessage} 的新实例。
     *
     * @param text 表示文本信息的 {@link String}。
     */
    protected AbstractChatMessage(String text) {
        this(Contents.from(text));
    }

    /**
     * 通过 {@link Contents} 来初始化 {@link AbstractChatMessage} 的新实例。
     *
     * @param contents 表示文本内容的 {@link Contents}。
     */
    protected AbstractChatMessage(Contents contents) {
        this.contents = contents;
    }

    @Override
    public String text() {
        return contents.text();
    }
}
