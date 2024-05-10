/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.parsers.nodes.filters;

import static com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowFilterType.MINIMUM_SIZE_FILTER;

import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.filters.FlowBatchSizeFilter;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.filters.FlowFilter;
import com.huawei.fit.jober.flowsengine.domain.flows.parsers.FlowGraphData;

/**
 * ClassName
 * 简述
 *
 * @author g00564732
 * @since 2023/09/25
 */
public class BatchSizeFilterParser implements FilterParser {
    @Override
    public FlowFilter parseFilter(FlowGraphData flowGraphData, int nodeIndex, String filterKey) {
        FlowFilter filter = new FlowBatchSizeFilter();
        filter.setFilterType(MINIMUM_SIZE_FILTER);
        commonParse(filter, flowGraphData, nodeIndex, filterKey);
        return filter;
    }
}
