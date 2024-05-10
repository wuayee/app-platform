/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.filters;

import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.Processors.Filter;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 最小Size的过滤器
 *
 * @author g00564732
 * @since 2023/09/25
 */
public class FlowBatchSizeFilter extends FlowFilter {
    private static final String THRESHOLD = "threshold";

    @Override
    public Filter<FlowData> filter() {
        int threshold = Integer.parseInt(this.getProperties().get(THRESHOLD));
        return (inputs) -> {
            inputs.forEach(input -> {
            });
            if (inputs.size() < threshold) {
                return new ArrayList<>();
            }
            return inputs.stream().limit(threshold).collect(Collectors.toList());
        };
    }
}
