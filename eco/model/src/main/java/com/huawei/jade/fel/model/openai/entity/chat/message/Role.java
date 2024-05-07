/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.model.openai.entity.chat.message;

import com.huawei.jade.fel.chat.MessageType;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * OpenAI 消息类型。
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024-4-30
 */
public enum Role {
    /**
     * 系统消息。
     */
    @JsonProperty("system")
    SYSTEM,

    /**
     * 用户消息。
     */
    @JsonProperty("user")
    USER,

    /**
     * 模型消息。
     */
    @JsonProperty("assistant")
    ASSISTANT,

    /**
     * 工具消息。
     */
    @JsonProperty("tool")
    TOOL,

    /**
     * 未知消息
     */
    @JsonProperty("unknown")
    UNKNOWN;

    /**
     * 将 {@link MessageType} 转换为 {@link Role} 。
     *
     * @param messageType FEL 消息类型
     * @return OpenAI消息类型 {@link Role} 。
     */
    public static Role generateRole(MessageType messageType) {
        if (messageType == null) {
            throw new IllegalArgumentException("Failed to generate OpenAI role: messageType is null.");
        }
        switch (messageType) {
            case SYSTEM:
                return Role.SYSTEM;
            case HUMAN:
                return Role.USER;
            case AI:
                return Role.ASSISTANT;
            case TOOL:
                return Role.TOOL;
            default:
                throw new IllegalArgumentException("Failed to generate OpenAI role: unknown messageType.");
        }
    }
}
