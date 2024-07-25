/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 表示工具调用的响应
 *
 * @author 程礼韬
 * @since 2024-06-29
 */
@Data
@AllArgsConstructor
public class ToolCallResponse {
    private String id;
    private String type;
    private FunctionCall function;
}

