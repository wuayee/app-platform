/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.tool;

import lombok.Data;

import java.util.Map;

/**
 * 表示可调用工具的实体类。
 *
 * @author 易文渊
 * @since 2024-4-8
 */
@Data
public class Tool {
    /**
     * 表示工具描述。
     */
    private Map<String, Object> schema;

    /**
     * 表示工具的自定义上下文信息。
     */
    private Map<String, Object> context;
}