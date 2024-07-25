/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 表示一个工具调用
 *
 * @author 程礼韬
 * @since 2024-06-29
 */
@Data
@AllArgsConstructor
public class ToolCall {
    private String type;
    private FunctionDefinition function;
}
