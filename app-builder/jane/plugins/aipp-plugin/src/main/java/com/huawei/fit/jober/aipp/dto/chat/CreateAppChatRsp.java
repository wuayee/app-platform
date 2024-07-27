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

/**
 * 创建app会话的响应结果
 *
 * @author 姚江 yWX1299574
 * @since 2024-07-23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateAppChatRsp {
    @Property(description = "chat id")
    @JsonProperty("chat_id")
    private String chatId;
}
