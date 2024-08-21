/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto.chat;

import com.huawei.fit.jober.aipp.constants.AippConst;
import modelengine.fitframework.annotation.Property;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 创建app会话的请求体
 *
 * @author 姚江
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
    private Context context;

    /**
     * 本类表示对话的上下文
     */
    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Context {
        @Property(description = "是否使用历史记录")
        @JsonProperty("use_memory")
        private Boolean useMemory;

        @Property(description = "用户自定义输入")
        @JsonProperty("user_context")
        private Map<String, Object> userContext;

        @Property(description = "at其它应用")
        @JsonProperty(AippConst.BS_AT_APP_ID)
        private String atAppId;

        @Property(description = "at其它应用的对话")
        @JsonProperty(AippConst.BS_AT_CHAT_ID)
        private String atChatId;

        @Property(description = "产品线的信息")
        @JsonProperty("dimension")
        private String dimension;
    }
}
