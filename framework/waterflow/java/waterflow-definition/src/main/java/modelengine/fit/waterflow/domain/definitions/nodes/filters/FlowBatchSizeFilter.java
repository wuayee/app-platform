/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.definitions.nodes.filters;

import modelengine.fit.waterflow.domain.context.FlowData;
import modelengine.fit.waterflow.domain.stream.operators.Operators;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 最小Size的过滤器
 *
 * @author 高诗意
 * @since 1.0
 */
public class FlowBatchSizeFilter extends FlowFilter {
    private static final String THRESHOLD = "threshold";

    @Override
    public Operators.Filter<FlowData> filter() {
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
