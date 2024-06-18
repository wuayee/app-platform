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

import java.util.List;

/**
 * 查询会话响应体
 *
 * @author z00597222
 * @since 2024-05-29
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryChatRsp {
    @Property(description = "app id")
    @JsonProperty("app_id")
    private String appId;

    @Property(description = "app version")
    @JsonProperty("app_version")
    private String version;

    @Property(description = "aipp id")
    @JsonProperty("aipp_id")
    private String aippId;

    @Property(description = "aipp version")
    @JsonProperty("aipp_version")
    private String aippVersion;

    @Property(description = "chat id")
    @JsonProperty("chat_id")
    private String chatId;

    @Property(description = "chat name")
    @JsonProperty("chat_name")
    private String chatName;

    @Property(description = "origin chat id")
    @JsonProperty("origin_chat_id")
    private String originChatId;

    @Property(description = "message list")
    @JsonProperty("msg_list")
    private List<MessageInfo> massageList;

    @Property(description = "current msg id")
    @JsonProperty("current_instance_id")
    private String msgId;

    @Property(description = "update time")
    @JsonProperty("update_time")
    private String updateTime;

    @Property(description = "recent info")
    @JsonProperty("recent_info")
    private String recentInfo;

    @Property(description = "update time")
    @JsonProperty("update_time_timestamp")
    private long updateTimeStamp;

    @Property(description = "current time")
    @JsonProperty("current_time_timestamp")
    private long currentTime;

    @Property(description = "total")
    @JsonProperty("total")
    private Integer total;
}
