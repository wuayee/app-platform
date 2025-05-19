/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers;

import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;

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
