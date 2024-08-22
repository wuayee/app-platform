/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.parsers.nodes.filters;

import static com.huawei.fit.waterflow.domain.enums.FlowFilterType.TRANS_BATCH_FILTER;

import com.huawei.fit.waterflow.domain.definitions.nodes.filters.FlowFilter;
import com.huawei.fit.waterflow.domain.definitions.nodes.filters.FlowTransBatchSizeFilter;
import com.huawei.fit.waterflow.domain.parsers.FlowGraphData;

/**
 * ClassName
 * 简述
 *
 * @author 夏斐
 * @since 1.0
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
