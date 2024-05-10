/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.converter;

import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowData;

/**
 * 节点间传递参数的转换器
 *
 * @author s00558940
 * @since 2024/4/17
 */
public interface FlowDataConverter {
    /**
     * 根据输入生成调用节点服务的入参
     *
     * @param input 当前输入
     * @return 调用节点服务的入参
     */
    FlowData convertInput(FlowData input);

    /**
     * 根据输出配置将结果放到FlowData中
     *
     * @param result
     * @param output
     * @return
     */
    FlowData convertOutput(Object result, FlowData output);
}
