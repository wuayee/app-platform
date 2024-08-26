/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.filters;

import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.Processors;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 最小Size的过滤器
 *
 * @author 高诗意
 * @since 2023/09/25
 */
public class FlowBatchSizeFilter extends FlowFilter {
    private static final String THRESHOLD = "threshold";

    @Override
    public Processors.Filter<FlowData> filter() {
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
