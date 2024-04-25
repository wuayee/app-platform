/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.chat;

import com.huawei.jade.fel.chat.content.Media;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示聊天大模型输入的接口。
 *
 * @author 易文渊
 * @since 2024-04-16
 */
public interface Prompt {
    /**
     * 获取输入的聊天消息列表。
     *
     * @return 表示聊天消息列表的 {@link List}{@code <}{@link ChatMessage}{@code >}。
     */
    List<ChatMessage> messages();

    /**
     * 获取聊天大模型的配置参数。
     *
     * @return 表示聊天大模型配置参数的 {@link ChatOptions}。
     */
    ChatOptions option();

    /**
     * 获取文本内容，如果存在多条消息，则使用{@code \n}进行拼接。
     *
     * @return 返回表示拼接文本内容的 {@link String}。
     */
    default String text() {
        return this.messages().stream().map(ChatMessage::text).collect(Collectors.joining("\n"));
    }

    /**
     * 获取媒体内容。
     *
     * @return 返回表示媒体内容的 {@link List}{@code <}{@link Media}{@code  >}。
     */
    default List<Media> medias() {
        return this.messages().stream().flatMap(m -> m.medias().stream()).collect(Collectors.toList());
    }
}