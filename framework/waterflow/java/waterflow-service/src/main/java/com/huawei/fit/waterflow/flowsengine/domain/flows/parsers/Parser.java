/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.parsers;

import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;

/**
 * 流程定义解析器
 * 负责解析前端的流程定义JSON数据
 *
 * @author 高诗意
 * @since 2023/08/14
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
