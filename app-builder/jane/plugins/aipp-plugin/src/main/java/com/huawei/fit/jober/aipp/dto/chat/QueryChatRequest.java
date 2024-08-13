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

/**
 * 查询会话请求提
 *
 *  @author 翟卉馨
 *  @since 2024-05-29
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryChatRequest {
    @Property(description = "aipp id")
    @JsonProperty("aipp_id")
    private String aippId;

    @Property(description = "aipp version")
    @JsonProperty("aipp_version")
    private String aippVersion;

    @Property(description = "offset")
    @JsonProperty("offset")
    private Integer offset;

    @Property(description = "limit")
    @JsonProperty("limit")
    private Integer limit;

    @Property(description = "app id")
    @JsonProperty("app_id")
    private String appId;

    @Property(description = "app version")
    @JsonProperty("app_version")
    private String appVersion;
}