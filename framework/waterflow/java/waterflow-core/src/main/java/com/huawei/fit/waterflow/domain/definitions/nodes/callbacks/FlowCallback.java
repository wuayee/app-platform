/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.definitions.nodes.callbacks;

import static com.huawei.fit.waterflow.common.ErrorCodes.FLOW_EXECUTE_CALLBACK_FITABLES_FAILED;

import com.huawei.fit.waterflow.common.exceptions.WaterflowException;
import com.huawei.fit.waterflow.domain.context.FlowContext;
import com.huawei.fit.waterflow.domain.context.FlowData;
import com.huawei.fit.waterflow.domain.enums.FlowCallbackType;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.log.Logger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 流程定义节点回调函数类
 *
 * @author l00862071
 * @since 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class FlowCallback {
    private static final Logger LOG = Logger.get(FlowCallback.class);

    /**
     * 所在节点的metaId
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
     * 执行回调函数核心方法
     *
     * @param inputs 流程实现执行时的入参
     */
    public void execute(List<FlowContext<FlowData>> inputs) {
        try {
            executeCallback(inputs);
        } catch (Throwable ex) {
            String fitableString = Optional.ofNullable(fitables).map(Object::toString).orElse("");
            LOG.error("Caught a throwable during a remote invocation, fitables are {}. Caused by {}.", fitableString,
                    ex.getMessage());
            throw new WaterflowException(FLOW_EXECUTE_CALLBACK_FITABLES_FAILED, this.name, this.type.getCode(),
                    fitableString, ex.getMessage());
        }
    }

    /**
     * 节点执行的回调
     *
     * @param inputs 当前处理的contexts列表
     * @throws Throwable 异常
     */
    protected abstract void executeCallback(List<FlowContext<FlowData>> inputs) throws Throwable;
}
