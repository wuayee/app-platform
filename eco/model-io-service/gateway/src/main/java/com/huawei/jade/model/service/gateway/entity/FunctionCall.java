/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 表示具体的函数调用
 *
 * @author 程礼韬
 * @since 2024-06-29
 */
@Data
@AllArgsConstructor
public class FunctionCall {
    private String name;
    private String arguments;
}

