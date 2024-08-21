/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.callbacks;

import static com.huawei.fit.jober.common.ErrorCodes.FLOW_EXECUTE_CALLBACK_FITABLES_FAILED;

import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.FlowDataConverter;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowCallbackType;
import com.huawei.fit.waterflow.flowsengine.domain.flows.utils.FlowExecuteInfoUtil;
import com.huawei.fit.waterflow.flowsengine.utils.FlowUtil;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.log.Logger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 流程定义回调函数类
 *
 * @author 李哲峰
 * @since 2023/12/11
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class FlowCallback {
    private static final Logger log = Logger.get(FlowCallback.class);

    private static final String CALLBACK_EXECUTE_INFO_TYPE = "callback";

    /**
     * 所在节点的metaId，如果为null则说明是整个流程的callback，每个可以callback的节点都会调用
     */
    protected String nodeMetaId;

    /**
     * 回调函数名称
     */
    protected String name;

    /**
     * 回调函数类型
     */
    protected FlowCallbackType type;

    /**
     * 回调函数filteredKeys集合
     * filteredKeys表示用户在回调过程中关心的业务数据key的集合
     */
    protected Set<String> filteredKeys;

    /**
     * 回调函数fitables集合
     */
    protected Set<String> fitables;

    /**
     * 回调函数属性，所有回调函数中定义的变量作为该属性的key
     */
    protected Map<String, String> properties;

    /**
     * 调用 fitable客户端
     */
    protected BrokerClient brokerClient;

    /**
     * 数据转换器
     */
    protected FlowDataConverter converter;

    /**
     * 执行回调函数核心方法
     *
     * @param inputs 流程实现执行时的入参
     */
    public void execute(List<FlowContext<FlowData>> inputs) {
        try {
            executeCallback(convertFlowData(inputs));
        } catch (FitException ex) {
            String fitableString = Optional.ofNullable(fitables).map(Object::toString).orElse("");
            log.error("Caught a throwable during a remote invocation, fitables are {}. Caused by {}.", fitableString,
                    ex.getMessage());
            throw new JobberException(FLOW_EXECUTE_CALLBACK_FITABLES_FAILED, this.name, this.type.getCode(),
                    fitableString, ex.getMessage());
        }
    }

    /**
     * executeCallback
     *
     * @param inputs inputs
     */
    protected abstract void executeCallback(List<FlowContext<FlowData>> inputs);

    private List<FlowContext<FlowData>> convertFlowData(List<FlowContext<FlowData>> inputs) {
        if (Objects.isNull(converter)) {
            return inputs;
        }
        return inputs.stream().map(input -> input.convertData(this.convertFlowData(input.getData()), input.getId()))
                .collect(Collectors.toList());
    }

    private FlowData convertFlowData(FlowData flowData) {
        Map<String, Object> newInputMap = converter.convertInput(flowData.getBusinessData());
        if (this.nodeMetaId != null) {
            FlowExecuteInfoUtil.addInputMap2ExecuteInfoMap(flowData, newInputMap, this.nodeMetaId,
                    CALLBACK_EXECUTE_INFO_TYPE);
        }
        flowData.setBusinessData(FlowUtil.mergeMaps(flowData.getBusinessData(), newInputMap));
        return flowData;
    }
}
