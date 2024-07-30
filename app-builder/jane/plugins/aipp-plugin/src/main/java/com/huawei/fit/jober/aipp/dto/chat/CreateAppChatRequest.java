/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto.chat;

import com.huawei.fitframework.annotation.Property;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 创建app会话的请求体
 *
 * @author 姚江 yWX1299574
 * @since 2024-07-23
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppChatRequest {
    @Property(description = "app的id")
    @JsonProperty("app_id")
    private String appId;

    @Property(description = "会话id")
    @JsonProperty("chat_id")
    private String chatId;

    @Property(description = "问题")
    @JsonProperty("question")
    private String question;

    @Property(description = "context")
    @JsonProperty("context")
    private Map<String, Object> context;
}
