/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.filters;

import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.filters.FlowFilter;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.FlowGraphData;

/**
 * 任务过滤器解析接口
 *
 * @author 杨祥宇
 * @since 2023/8/15
 */
public interface FilterParser {
    /**
     * 解析过滤器
     *
     * @param flowGraphData {@link FlowGraphData} 流程json操作封装对象
     * @param nodeIndex 当前节点索引
     * @param filterKey 过滤器的key
     * @return 过滤器
     */
    FlowFilter parseFilter(FlowGraphData flowGraphData, int nodeIndex, String filterKey);

    /**
     * commonParse
     *
     * @param flowFilter flowJobber
     * @param flowGraphData flowGraphData
     * @param nodeIndex nodeIndex
     * @param filterKey filterKey
     */
    default void commonParse(FlowFilter flowFilter, FlowGraphData flowGraphData, int nodeIndex, String filterKey) {
        flowFilter.setProperties(flowGraphData.getNodeFilterProperties(nodeIndex, filterKey));
    }
}
