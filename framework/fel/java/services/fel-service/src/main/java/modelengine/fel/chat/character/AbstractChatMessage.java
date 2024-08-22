/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.chat.character;

import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fel.chat.ChatMessage;

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
    private final String content;

    /**
     * 通过文本信息来初始化 {@link AbstractChatMessage} 的新实例。
     *
     * @param text 表示文本信息的 {@link String}。
     */
    protected AbstractChatMessage(String text) {
        this.content = ObjectUtils.nullIf(text, StringUtils.EMPTY);
    }

    @Override
    public String text() {
        return this.content;
    }

    @Override
    public String toString() {
        return this.type().getRole() + ": " + this.text();
    }
}
