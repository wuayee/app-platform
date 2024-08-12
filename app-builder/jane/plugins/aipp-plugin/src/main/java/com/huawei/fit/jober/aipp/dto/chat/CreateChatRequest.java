/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
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
 * 创建会话请求体
 *
 * @author 翟卉馨
 * @since 2024-05-29
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatRequest {
    @Property(description = "aipp_id")
    @JsonProperty("aipp_id")
    private String aippId;

    @Property(description = "aipp_version")
    @JsonProperty("aipp_version")
    private String aippVersion;

    @Property(description = "init context")
    @JsonProperty("init_context")
    private Map<String, Object> initContext;

    @Property(description = "chat_id")
    @JsonProperty("chat_id")
    private String chatId;

    @Property(description = "origin_app")
    @JsonProperty("origin_app")
    private String originApp;

    @Property(description = "origin_app_version")
    @JsonProperty("origin_app_version")
    private String originAppVersion;
}
