/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.parsers;

import com.huawei.fit.waterflow.domain.definitions.FlowDefinition;

/**
 * 流程定义解析器
 * 负责解析前端的流程定义JSON数据
 *
 * @author g00564732
 * @since 1.0
 */
public interface Parser {
    /**
     * 解析接口
     * 负责将前端JSON GRAPH数据解析成 {@link FlowDefinition}对象
     *
     * @param flowDefinition 前端给的流程定义graph
     * @return {@link FlowDefinition}
     */
    FlowDefinition parse(String flowDefinition);
}
