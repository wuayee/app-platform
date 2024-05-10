/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.parsers.nodes.filters;

import static com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowFilterType.TRANS_BATCH_FILTER;

import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.filters.FlowFilter;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.filters.FlowTransBatchSizeFilter;
import com.huawei.fit.jober.flowsengine.domain.flows.parsers.FlowGraphData;

/**
 * ClassName
 * 简述
 *
 * @author x00576283
 * @since 2023/11/27
 */
public class TransBatchSizeFilterParser implements FilterParser {
    @Override
    public FlowFilter parseFilter(FlowGraphData flowGraphData, int nodeIndex, String filterKey) {
        FlowFilter filter = new FlowTransBatchSizeFilter();
        filter.setFilterType(TRANS_BATCH_FILTER);
        commonParse(filter, flowGraphData, nodeIndex, filterKey);
        return filter;
    }
}
