/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.parsers.nodes.callbacks;

import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.callbacks.FlowCallback;
import com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowDataConverterType;
import com.huawei.fit.jober.flowsengine.domain.flows.parsers.FlowGraphData;

import java.util.HashSet;
import java.util.Optional;

/**
 * 回调函数解析接口
 *
 * @author l00862071
 * @since 2023/12/13
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
        Optional.ofNullable(flowGraphData.getNodeCallbackConverter(nodeIndex))
                .ifPresent(config -> flowCallback.setConverter(
                        FlowDataConverterType.getType((String) config.get("type")).getParser().parse(config)));
    }
}
