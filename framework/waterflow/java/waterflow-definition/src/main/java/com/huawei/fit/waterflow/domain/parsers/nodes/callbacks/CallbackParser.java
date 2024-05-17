/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.parsers.nodes.callbacks;

import com.huawei.fit.waterflow.domain.definitions.nodes.callbacks.FlowCallback;
import com.huawei.fit.waterflow.domain.parsers.FlowGraphData;

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
