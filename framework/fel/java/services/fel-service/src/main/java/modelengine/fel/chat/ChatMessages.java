/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 聊天消息的集合。
 *
 * @author 刘信宏
 * @since 2024-05-06
 */
public class ChatMessages implements Prompt {
    private final List<ChatMessage> messages = new ArrayList<>();

    /**
     * 使用聊天消息创建 {@link ChatMessages} 的实例。
     *
     * @param message 表示聊天消息的 {@link ChatMessage}{@code []}。
     * @return 表示创建成功的 {@link ChatMessages}。
     */
    public static ChatMessages from(ChatMessage... message) {
        return ChatMessages.from(Arrays.asList(message));
    }

    /**
     * 使用聊天消息数组创建 {@link ChatMessages} 的实例。
     *
     * @param messages 表示聊天消息的 {@link List}{@code <? extends }{@link ChatMessage}{@code >}。
     * @return 表示创建成功的 {@link ChatMessages}。
     */
    public static ChatMessages from(List<? extends ChatMessage> messages) {
        ChatMessages chatMessages = new ChatMessages();
        chatMessages.messages().addAll(messages);
        return chatMessages;
    }

    /**
     * 添加聊天消息。
     *
     * @param message 表示聊天消息的 {@link ChatMessage}。
     * @return 表示添加消息成功的 {@link ChatMessages}。
     */
    public ChatMessages add(ChatMessage message) {
        this.messages.add(message);
        return this;
    }

    /**
     * 批量添加聊天消息。
     *
     * @param messages 表示聊天消息数组的 {@link List}{@code <}{@link ChatMessage}{@code >}。
     * @return 表示添加消息成功的 {@link ChatMessages}。
     */
    public ChatMessages addAll(List<? extends ChatMessage> messages) {
        this.messages.addAll(messages);
        return this;
    }

    /**
     * 合并另一个聊天消息集合。
     *
     * @param other 表示聊天消息集合实例的 {@link ChatMessages}。
     * @return 表示添加消息成功的 {@link ChatMessages}。
     */
    public ChatMessages merge(ChatMessages other) {
        this.messages.addAll(other.messages());
        return this;
    }

    @Override
    public List<ChatMessage> messages() {
        return messages;
    }
}
