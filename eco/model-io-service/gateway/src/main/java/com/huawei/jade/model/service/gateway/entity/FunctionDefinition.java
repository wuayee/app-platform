/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.Optional;

/**
 * 表示一个函数定义
 *
 * @author 程礼韬
 * @since 2024-06-29
 */
@Data
@AllArgsConstructor
public class FunctionDefinition {
    private String name;
    private String description;
    private Optional<Map<String, Object>> parameters;
}

