/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.parsers.nodes.filters;

import static com.huawei.fit.waterflow.domain.enums.FlowFilterType.MINIMUM_SIZE_FILTER;

import com.huawei.fit.waterflow.domain.definitions.nodes.filters.FlowBatchSizeFilter;
import com.huawei.fit.waterflow.domain.definitions.nodes.filters.FlowFilter;
import com.huawei.fit.waterflow.domain.parsers.FlowGraphData;

/**
 * ClassName
 * 简述
 *
 * @author g00564732
 * @since 1.0
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
