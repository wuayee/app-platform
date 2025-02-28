/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.callbacks;

import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.callbacks.FlowCallback;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.callbacks.FlowGeneralCallback;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowCallbackType;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.FlowGraphData;

/**
 * 通用型回调函数解析类
 *
 * @author 李哲峰
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
