/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.entity;

import com.huawei.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话记录实体
 *
 * @author 吴穹
 * @since 2024-05-029
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatInfo {
    @Property(description = "app_id")
    private String appId;

    @Property(description = "app_version")
    private String version;

    @Property(description = "id")
    private String chatId;

    @Property(description = "name")
    private String chatName;

    @Property(description = "attributes")
    private String attributes;

    @Property(description = "create_at")
    private LocalDateTime createTime;

    @Property(description = "create_by")
    private String creator;

    @Property(description = "update_at")
    private LocalDateTime updateTime;

    @Property(description = "update_by")
    private String updater;

    @Property(description = "status")
    private Integer status;
}
