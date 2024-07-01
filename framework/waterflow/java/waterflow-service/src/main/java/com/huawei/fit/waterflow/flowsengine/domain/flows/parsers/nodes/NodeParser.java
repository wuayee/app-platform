/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.nodes;

import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.FlowGraphData.JOBER_FILTER;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.FlowGraphData.TASK_FILTER;

import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.callbacks.FlowCallback;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.filters.FlowFilter;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.tasks.FlowTask;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowCallbackType;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowFilterType;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowJoberType;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeTriggerMode;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowTaskType;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.FlowGraphData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.nodes.callbacks.CallbackParser;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.nodes.filters.FilterParser;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.nodes.jobers.JoberParser;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.nodes.tasks.TaskParser;
import com.huawei.fitframework.inspection.Validation;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Optional;

/**
 * 流程节点解析接口
 *
 * @author y00679285
 * @since 2023/8/15
 */
public interface NodeParser {
    /**
     * 按照规则解析节点
     *
     * @param flowGraphData {@link FlowGraphData} 流程json操作封装对象
     * @param index {@link int} node索引
     * @return {@link FlowNode} FlowNode类对象
     */
    FlowNode parseNode(FlowGraphData flowGraphData, int index);

    /**
     * 节点统一解析操作
     *
     * @param flowNode {@link FlowNode} 流程节点对象
     * @param flowGraphData {@link FlowGraphData} 流程json操作封装对象
     * @param index 当前节点索引
     */
    default void commonParse(FlowNode flowNode, FlowGraphData flowGraphData, int index) {
        flowNode.setName(flowGraphData.getNodeName(index));
        flowNode.setMetaId(flowGraphData.getNodeMetaId(index));
        flowNode.setEvents(new ArrayList<>());
        flowNode.setProperties(flowGraphData.getNodeProperties(index));
        flowNode.setTriggerMode(FlowNodeTriggerMode.valueOf(flowGraphData.getNodeTriggerMode(index)));
        flowNode.setJober(parseJober(flowGraphData, index));
        flowNode.setJoberFilter(parseFilter(flowGraphData, index, JOBER_FILTER));
        flowNode.setTask(parseTask(flowGraphData, index));
        flowNode.setTaskFilter(parseFilter(flowGraphData, index, TASK_FILTER));
        flowNode.setCallback(parseCallback(flowGraphData, index));
        flowNode.setExceptionFitables(flowGraphData.getFlowExceptionFitables());
    }

    /**
     * 解析自动任务默认实现
     *
     * @param flowGraphData {@link FlowGraphData} 流程json操作封装对象
     * @param nodeIndex 当前节点索引
     * @return 当前节点任务列表
     */
    default FlowJober parseJober(FlowGraphData flowGraphData, int nodeIndex) {
        JSONObject nodeJobber = flowGraphData.getNodeJober(nodeIndex);
        if (!Optional.ofNullable(nodeJobber).isPresent()) {
            return null;
        }

        FlowJoberType joberType = FlowJoberType.getJoberType(flowGraphData.getNodeJoberType(nodeIndex));
        JoberParser joberParser = joberType.getJoberParser();
        Validation.notNull(joberParser,
                () -> new JobberParamException(INPUT_PARAM_IS_INVALID, "flow jober type " + joberType.getCode()));
        return joberParser.parseJober(flowGraphData, nodeIndex);
    }

    /**
     * 解析人工任务默认实现
     *
     * @param flowGraphData {@link FlowGraphData} 流程json操作封装对象
     * @param nodeIndex 当前节点索引
     * @return 当前节点任务列表
     */
    default FlowTask parseTask(FlowGraphData flowGraphData, int nodeIndex) {
        JSONObject nodeTask = flowGraphData.getNodeTask(nodeIndex);
        if (!Optional.ofNullable(nodeTask).isPresent()) {
            return null;
        }

        Optional<String> nodeTaskTypeOption = flowGraphData.getNodeTaskType(nodeIndex);
        if (!nodeTaskTypeOption.isPresent()) {
            return null;
        }
        FlowTaskType taskType = FlowTaskType.getTaskType(nodeTaskTypeOption.get());
        TaskParser taskParser = taskType.getTaskParser();
        Validation.notNull(taskParser,
                () -> new JobberParamException(INPUT_PARAM_IS_INVALID, "flow task type " + taskType.getCode()));
        return taskParser.parseTask(flowGraphData, nodeIndex);
    }

    /**
     * 解析过滤器默认实现
     *
     * @param flowGraphData {@link FlowGraphData} 流程json操作封装对象
     * @param nodeIndex 当前节点索引
     * @param filterKey filterKey
     * @return 当前节点过滤器
     */
    default FlowFilter parseFilter(FlowGraphData flowGraphData, int nodeIndex, String filterKey) {
        JSONObject filter = flowGraphData.getNodeFilter(nodeIndex, filterKey);
        if (!Optional.ofNullable(filter).isPresent()) {
            return null;
        }

        Optional<String> nodeFilterTypeOption = flowGraphData.getNodeFilterType(nodeIndex, filterKey);
        if (!nodeFilterTypeOption.isPresent()) {
            return null;
        }
        FlowFilterType filterType = FlowFilterType.getFilterType(nodeFilterTypeOption.get());
        FilterParser filterParser = filterType.getFilterParser();
        Validation.notNull(filterParser,
                () -> new JobberParamException(INPUT_PARAM_IS_INVALID, "flow filter type " + filterType.getCode()));
        return filterParser.parseFilter(flowGraphData, nodeIndex, filterKey);
    }

    /**
     * 解析回调函数默认实现
     *
     * @param flowGraphData {@link FlowGraphData} 流程json操作封装对象
     * @param nodeIndex 当前节点索引
     * @return 当前节点回调函数
     */
    default FlowCallback parseCallback(FlowGraphData flowGraphData, int nodeIndex) {
        JSONObject nodeCallback = flowGraphData.getNodeCallback(nodeIndex);
        if (!Optional.ofNullable(nodeCallback).isPresent()) {
            return null;
        }

        Optional<String> nodeCallbackTypeOption = flowGraphData.getNodeCallbackType(nodeIndex);
        if (!nodeCallbackTypeOption.isPresent()) {
            return null;
        }
        FlowCallbackType callbackType = FlowCallbackType.getCallbackType(nodeCallbackTypeOption.get());
        CallbackParser callbackParser = callbackType.getCallbackParser();
        Validation.notNull(callbackParser,
                () -> new JobberParamException(INPUT_PARAM_IS_INVALID, "flow callback type " + callbackType.getCode()));
        return callbackParser.parseNodeCallback(flowGraphData, nodeIndex);
    }
}
