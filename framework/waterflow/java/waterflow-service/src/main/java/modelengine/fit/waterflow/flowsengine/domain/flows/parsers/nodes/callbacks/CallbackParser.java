/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.callbacks;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.callbacks.FlowCallback;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowDataConverterType;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.FlowGraphData;

import java.util.HashSet;
import java.util.Optional;

/**
 * 回调函数解析接口
 *
 * @author 李哲峰
 * @since 2023/12/13
 */
public interface CallbackParser {
    /**
     * 按照回调函数规则解析某个节点的回调函数
     *
     * @param flowGraphData {@link FlowGraphData} 流程json操作封装对象
     * @param nodeIndex 当前节点索引
     * @return 流程节点任务对象
     */
    FlowCallback parseNodeCallback(FlowGraphData flowGraphData, int nodeIndex);

    /**
     * 按照回调函数规则解析回调函数。
     *
     * @param flowGraphData 表示流程json操作封装对象的 {@link FlowGraphData}。
     * @return 作用于每一个有callback能力节点的流程任务对象。
     */
    FlowCallback parseCallback(FlowGraphData flowGraphData);

    /**
     * 公共解析设置
     *
     * @param flowCallback flowCallback 表示流程定义回调函数的 {@link FlowCallback}。
     * @param flowGraphData flowGraphData 表示流程json操作封装对象的 {@link FlowGraphData}。
     * @param isNodeCallback 表示是否为节点级回调函数的boolean。
     * @param nodeIndex nodeIndex 表示节点index的 {@link Integer}。
     */
    default void commonParse(FlowCallback flowCallback, FlowGraphData flowGraphData, boolean isNodeCallback,
            Integer nodeIndex) {
        initializeCallback(flowCallback);
        if (isNodeCallback) {
            setNodeCallbackProperties(flowCallback, flowGraphData, nodeIndex);
        } else {
            setFlowCallbackProperties(flowCallback, flowGraphData);
        }
    }

    /**
     * 初始化回调对象的集合属性。
     *
     * @param flowCallback flowCallback 表示流程定义回调函数的 {@link FlowCallback}。
     */
    default void initializeCallback(FlowCallback flowCallback) {
        flowCallback.setFilteredKeys(new HashSet<>());
        flowCallback.setFitables(new HashSet<>());
    }

    /**
     * 设置节点回调属性。
     *
     * @param flowCallback flowCallback 表示流程定义回调函数的 {@link FlowCallback}。
     * @param flowGraphData flowGraphData 表示流程json操作封装对象的 {@link FlowGraphData}。
     * @param nodeIndex nodeIndex 表示节点index的int。
     */
    default void setNodeCallbackProperties(FlowCallback flowCallback, FlowGraphData flowGraphData, int nodeIndex) {
        flowCallback.setNodeMetaId(flowGraphData.getNodeMetaId(nodeIndex));
        flowCallback.setName(flowGraphData.getNodeCallbackName(nodeIndex));
        flowCallback.setProperties(flowGraphData.getNodeCallbackProperties(nodeIndex));
        Optional.ofNullable(flowGraphData.getNodeCallbackConverter(nodeIndex))
                .ifPresent(config -> flowCallback.setConverter(
                        FlowDataConverterType.getType(cast(config.get("type"))).getParser().parse(config)));
    }

    /**
     * 设置流程回调属性。
     *
     * @param flowCallback flowCallback 表示流程定义回调函数的 {@link FlowCallback}。
     * @param flowGraphData flowGraphData 表示流程json操作封装对象的 {@link FlowGraphData}。
     */
    default void setFlowCallbackProperties(FlowCallback flowCallback, FlowGraphData flowGraphData) {
        flowCallback.setName(flowGraphData.getFlowCallbackName());
        flowCallback.setProperties(flowGraphData.getFlowCallbackProperties());
        Optional.ofNullable(flowGraphData.getFlowCallbackConverter())
                .ifPresent(config -> flowCallback.setConverter(
                        FlowDataConverterType.getType(cast(config.get("type"))).getParser().parse(config)));
    }
}
