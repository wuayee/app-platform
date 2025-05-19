/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.parsers.nodes.callbacks;

import modelengine.fit.waterflow.domain.definitions.nodes.callbacks.FlowCallback;
import modelengine.fit.waterflow.domain.parsers.FlowGraphData;

import java.util.HashSet;

/**
 * 回调函数解析接口
 *
 * @author 李哲峰
 * @since 1.0
 */
public interface CallbackParser {
    /**
     * 按照回调函数规则解析回调函数
     *
     * @param flowGraphData {@link FlowGraphData} 流程json操作封装对象
     * @param nodeIndex 当前节点索引
     * @return 流程节点任务对象
     */
    FlowCallback parseCallback(FlowGraphData flowGraphData, int nodeIndex);

    /**
     * 公共解析设置
     *
     * @param flowCallback flowCallback
     * @param flowGraphData flowGraphData
     * @param nodeIndex nodeIndex
     */
    default void commonParse(FlowCallback flowCallback, FlowGraphData flowGraphData, int nodeIndex) {
        flowCallback.setNodeMetaId(flowGraphData.getNodeMetaId(nodeIndex));
        flowCallback.setName(flowGraphData.getNodeCallbackName(nodeIndex));
        flowCallback.setFilteredKeys(new HashSet<>());
        flowCallback.setFitables(new HashSet<>());
        flowCallback.setProperties(flowGraphData.getNodeCallbackProperties(nodeIndex));
    }
}
