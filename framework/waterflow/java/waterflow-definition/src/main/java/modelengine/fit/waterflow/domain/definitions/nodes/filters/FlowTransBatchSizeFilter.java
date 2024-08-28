/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.definitions.nodes.filters;

import modelengine.fit.waterflow.domain.context.FlowContext;
import modelengine.fit.waterflow.domain.context.FlowData;
import modelengine.fit.waterflow.domain.stream.operators.Operators;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 同一个trans下的每批次Size的过滤器，如果最终剩余的数量不满足要求，则
 *
 * @author 夏斐
 * @since 1.0
 */
public class FlowTransBatchSizeFilter extends FlowFilter {
    private static final String THRESHOLD = "threshold";

    private final Operators.Filter<FlowData> batchSizeFilter = (List<FlowContext<FlowData>> contexts) -> {
        if (CollectionUtils.isEmpty(contexts)) {
            return new ArrayList<>();
        }
        int threshold = Integer.parseInt(this.getProperties().get(THRESHOLD));
        String batchId = contexts.stream()
                .filter(context -> StringUtils.isNotEmpty(context.getBatchId()))
                .findAny()
                .map(FlowContext::getBatchId)
                .orElse("");
        return contexts.stream()
                .filter(context -> StringUtils.isNotEmpty(context.getBatchId()))
                .filter(context -> context.getBatchId().equals(batchId))
                .limit(threshold)
                .collect(Collectors.toList());
    };

    @Override
    public Operators.Filter<FlowData> filter() {
        return batchSizeFilter;
    }
}
