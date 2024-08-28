/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.chat;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 表示消息类型的枚举。
 *
 * @author 易文渊
 * @since 2024-04-16
 */
@Getter
public enum MessageType {
    /**
     * 表示系统消息。
     */
    SYSTEM("system"),

    /**
     * 表示人类消息。
     */
    HUMAN("human"),

    /**
     * 表示人工智能消息。
     */
    AI("ai"),

    /**
     * 表示工具消息。
     */
    TOOL("tool");

    private static final Map<String, MessageType> RELATIONSHIP =
        Arrays.stream(MessageType.values()).collect(Collectors.toMap(MessageType::getRole, Function.identity()));

    private final String role;

    MessageType(String role) {
        this.role = role;
    }

    /**
     * 根据字符串获取 {@link MessageType} 的实例。
     *
     * @param role 表示消息角色的 {@link String}。
     * @return 表示消息类型的 {@link MessageType}。
     */
    public static MessageType parse(String role) {
        return RELATIONSHIP.getOrDefault(role, MessageType.HUMAN);
    }
}