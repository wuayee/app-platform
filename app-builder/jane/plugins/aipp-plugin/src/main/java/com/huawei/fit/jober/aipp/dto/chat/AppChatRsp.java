/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

import java.util.List;

/**
 * 创建app会话的响应结果
 *
 * @author 姚江
 * @since 2024-07-23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppChatRsp {
    @Property(description = "chat id")
    @JsonProperty("chat_id")
    private String chatId;

    @Property(description = "at chat id")
    @JsonProperty("at_chat_id")
    private String atChatId;

    private String status;

    private List<Answer> answer;

    @Property(description = "form instance id")
    @JsonProperty("instance_id")
    private String instanceId;

    @Property(description = "log id")
    @JsonProperty("log_id")
    private String logId;

    /**
     * Answer
     *
     * @author 姚江
     * @since 2024-07-29
     */
    @Data
    @Builder
    public static class Answer {
        Object content;
        String type;
        String msgId;
    }
}
