/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto.chat;

import modelengine.fitframework.annotation.Property;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 会话消息
 *
 * @author 翟卉馨
 * @since 2024-05-29
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageInfo {
    @Property(description = "message id")
    @JsonProperty("message_id")
    private String msgId;

    @Property(description = "role")
    @JsonProperty("role")
    private String role;

    @Property(description = "create time")
    @JsonProperty("create_time")
    private String createTime;

    @Property(description = "content type")
    @JsonProperty("content_type")
    private Integer contentType;

    @Property(description = "content")
    @JsonProperty("content")
    private List<String> content;

    @Property(description = "parent id")
    @JsonProperty("parent_id")
    private String parentId;

    @Property(description = "children id")
    @JsonProperty("children_id")
    private String childrenId;
    @Property(description = "app name")
    @JsonProperty("app_name")
    private String appName;

    @Property(description = "app icon")
    @JsonProperty("app_icon")
    private String appIcon;
}