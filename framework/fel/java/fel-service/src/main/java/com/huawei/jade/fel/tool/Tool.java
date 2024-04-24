/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.tool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 表示可调用工具的实体类。
 *
 * @author 易文渊
 * @since 2024-4-8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tool {
    /**
     * 表示是否是异步工具。
     */
    private boolean isAsync;

    /**
     * 表示工具描述。
     */
    private Map<String, Object> schema;
}