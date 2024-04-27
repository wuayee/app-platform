/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.chat.charactar;

import com.huawei.jade.fel.chat.MessageType;
import com.huawei.jade.fel.chat.content.Contents;
import com.huawei.jade.fel.chat.content.Media;

import java.util.List;

/**
 * 表示人类消息的实现。
 *
 * @author 刘信宏
 * @since 2024-4-12
 */
public class HumanMessage extends AbstractChatMessage {
    /**
     * 通过文本信息来初始化 {@link HumanMessage} 的新实例。
     *
     * @param text 表示文本信息的 {@link String}。
     */
    public HumanMessage(String text) {
        super(text);
    }

    /**
     * 通过 {@link Contents} 来初始化 {@link HumanMessage} 的新实例。
     *
     * @param contents 表示消息内容的 {@link Contents}。
     */
    public HumanMessage(Contents contents) {
        super(contents);
    }

    @Override
    public MessageType type() {
        return MessageType.HUMAN;
    }

    @Override
    public List<Media> medias() {
        return this.contents.medias();
    }
}
