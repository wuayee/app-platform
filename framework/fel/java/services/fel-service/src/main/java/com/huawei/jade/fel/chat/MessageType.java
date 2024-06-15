/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.chat;

/**
 * 表示消息类型的枚举。
 *
 * @author 易文渊
 * @since 2024-04-16
 */
public enum MessageType {
    /**
     * 表示系统消息。
     */
    SYSTEM,

    /**
     * 表示人类消息。
     */
    HUMAN,

    /**
     * 表示人工智能消息。
     */
    AI,

    /**
     * 表示工具消息。
     */
    TOOL
}