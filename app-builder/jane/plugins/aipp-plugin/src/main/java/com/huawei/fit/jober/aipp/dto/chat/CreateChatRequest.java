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
 * @author z00597222
 * @since 2024-05-29
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatRequest {
    @Property(description = "app_id")
    @JsonProperty("app_id")
    private String aippId;

    @Property(description = "app_version")
    @JsonProperty("app_version")
    private String version;

    @Property(description = "init context")
    @JsonProperty("init_context")
    private Map<String, Object> initContext;
}
