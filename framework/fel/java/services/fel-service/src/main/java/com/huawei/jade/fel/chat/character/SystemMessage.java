/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.chat.character;

import com.huawei.jade.fel.chat.MessageType;

/**
 * 表现系统消息的实现。
 *
 * @author 刘信宏
 * @since 2024-4-12
 */
public class SystemMessage extends AbstractChatMessage {
    /**
     * 通过文本信息来初始化 {@link SystemMessage} 的新实例。
     *
     * @param text 表示文本信息的 {@link String}。
     */
    public SystemMessage(String text) {
        super(text);
    }

    @Override
    public MessageType type() {
        return MessageType.SYSTEM;
    }
}
