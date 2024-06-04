/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.parsers.nodes.callbacks;

import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.callbacks.FlowCallback;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.callbacks.FlowGeneralCallback;
import com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowCallbackType;
import com.huawei.fit.jober.flowsengine.domain.flows.parsers.FlowGraphData;

/**
 * 通用型回调函数解析类
 *
 * @author l00862071
 * @since 2023/12/11
 */
public class GeneralCallbackParser implements CallbackParser {
    /**
     * 按照回调函数规则解析回调函数
     *
     * @param flowGraphData {@link FlowGraphData} 流程json操作封装对象
     * @param nodeIndex 当前节点索引
     * @return 流程节点回调函数对象
     */
    @Override
    public FlowCallback parseNodeCallback(FlowGraphData flowGraphData, int nodeIndex) {
        FlowCallback flowCallback = createAndInitializeCallback();
        commonParse(flowCallback, flowGraphData, true, nodeIndex);
        setNodeSpecificProperties(flowCallback, flowGraphData, nodeIndex);
        return flowCallback;
    }

    @Override
    public FlowCallback parseCallback(FlowGraphData flowGraphData) {
        FlowCallback flowCallback = createAndInitializeCallback();
        // Passing -1 as nodeIndex is unused for flow callbacks
        commonParse(flowCallback, flowGraphData, false, -1);
        setFlowSpecificProperties(flowCallback, flowGraphData);
        return flowCallback;
    }

    private FlowCallback createAndInitializeCallback() {
        FlowCallback flowCallback = new FlowGeneralCallback();
        flowCallback.setType(FlowCallbackType.GENERAL_CALLBACK);
        return flowCallback;
    }

    private void setNodeSpecificProperties(FlowCallback flowCallback, FlowGraphData flowGraphData, int nodeIndex) {
        flowCallback.setFilteredKeys(flowGraphData.getNodeCallbackFilteredKeys(nodeIndex));
        flowCallback.setFitables(flowGraphData.getNodeCallbackFitables(nodeIndex));
    }

    private void setFlowSpecificProperties(FlowCallback flowCallback, FlowGraphData flowGraphData) {
        flowCallback.setFilteredKeys(flowGraphData.getFlowCallbackFilteredKeys());
        flowCallback.setFitables(flowGraphData.getFlowCallbackFitables());
    }
}
