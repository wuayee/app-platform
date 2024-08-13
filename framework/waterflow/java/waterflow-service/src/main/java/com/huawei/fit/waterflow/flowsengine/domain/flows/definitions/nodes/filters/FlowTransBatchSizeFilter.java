/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.filters;

import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.Processors;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 同一个trans下的每批次Size的过滤器，如果最终剩余的数量不满足要求，则
 *
 * @author 夏斐
 * @since 2023/11/28
 */
public class FlowTransBatchSizeFilter extends FlowFilter {
    private static final String THRESHOLD = "threshold";

    private final Processors.Filter<FlowData> batchSizeFilter = (List<FlowContext<FlowData>> contexts) -> {
        if (CollectionUtils.isEmpty(contexts)) {
            return new ArrayList<>();
        }
        int threshold = Integer.parseInt(this.getProperties().get(THRESHOLD));
        String batchId = contexts.stream()
                .filter(c -> StringUtils.isNotEmpty(c.getBatchId()))
                .findAny()
                .map(FlowContext::getBatchId)
                .orElse("");
        return contexts.stream()
                .filter(c -> StringUtils.isNotEmpty(c.getBatchId()))
                .filter(c -> c.getBatchId().equals(batchId))
                .limit(threshold)
                .collect(Collectors.toList());
    };

    @Override
    public Processors.Filter<FlowData> filter() {
        return batchSizeFilter;
    }
}
