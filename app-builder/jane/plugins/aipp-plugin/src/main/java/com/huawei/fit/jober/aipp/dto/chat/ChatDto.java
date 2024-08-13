/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto.chat;

import com.huawei.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会话信息
 *
 * @author 翟卉馨
 * @since 2024-05-29
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
    @Property(description = "message id")
    private String msgId;

    @Property(description = "logType")
    private String logType;

    @Property(description = "create time")
    private String createTime;

    @Property(description = "msg")
    private String logData;

    @Property(description = "aipp id")
    private String aippId;

    @Property(description = "app name")
    private String appName;

    @Property(description = "app icon")
    private String appIcon;
}