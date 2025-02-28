/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.core.memory;

import modelengine.fel.core.chat.ChatMessage;

import java.util.List;

/**
 * 对话历史记录。
 *
 * @author 刘信宏
 * @since 2024-04-28
 */
public interface Memory {
    /**
     * 插入一条历史记录。
     *
     * @param question 表示问题描述的 {@link ChatMessage}。
     * @param answer 表示回答内容的 {@link ChatMessage}。
     */
    void add(ChatMessage question, ChatMessage answer);

    /**
     * 获取历史记录的问答对列表。
     *
     * @return 表示问答对列表的 {@link List}{@code <}{@link ChatMessage}{@code >}。
     */
    List<ChatMessage> messages();

    /**
     * 获取历史记录中问答对内容格式化的文本。
     *
     * @return 表示历史记录文本的 {@link String}。
     */
    String text();
}
