/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.filters;

import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowFilterType;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.Processors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * 流程节点数据过滤器
 *
 * @author g00564732
 * @since 2023/09/25
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class FlowFilter {
    private FlowFilterType filterType;

    private Map<String, String> properties;

    /**
     * filter
     *
     * @return Filter<FlowData>
     */
    public abstract Processors.Filter<FlowData> filter();
}
