/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 对话VO
 *
 * @author 姚江 yWX1299574
 * @since 2024-07-27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageVo {
    private String data;

    private String type;

    @JsonProperty("message_id")
    private String messageId;

    private String status;

    private Map<String, Object> extension;
}
