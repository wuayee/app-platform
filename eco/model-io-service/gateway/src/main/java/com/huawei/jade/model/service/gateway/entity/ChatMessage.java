/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Optional;

/**
 * 表示一个聊天消息
 *
 * @author 程礼韬
 * @since 2024-06-29
 */
@Data
@AllArgsConstructor
public class ChatMessage {
    private String role;

    private Optional<String> content;

    private Optional<String> name;

    private Optional<List<ToolCall>> tools;

    private Optional<String> toolChoice;

    private Optional<List<ToolCallResponse>> toolCalls;
}

