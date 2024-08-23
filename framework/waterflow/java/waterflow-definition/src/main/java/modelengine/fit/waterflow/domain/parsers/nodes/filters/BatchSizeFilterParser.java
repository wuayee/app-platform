/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.parsers.nodes.filters;

import modelengine.fit.waterflow.domain.definitions.nodes.filters.FlowBatchSizeFilter;
import modelengine.fit.waterflow.domain.definitions.nodes.filters.FlowFilter;
import modelengine.fit.waterflow.domain.parsers.FlowGraphData;
import modelengine.fit.waterflow.domain.enums.FlowFilterType;

/**
 * ClassName
 * 简述
 *
 * @author 高诗意
 * @since 1.0
 */
public class BatchSizeFilterParser implements FilterParser {
    @Override
    public FlowFilter parseFilter(FlowGraphData flowGraphData, int nodeIndex, String filterKey) {
        FlowFilter filter = new FlowBatchSizeFilter();
        filter.setFilterType(FlowFilterType.MINIMUM_SIZE_FILTER);
        commonParse(filter, flowGraphData, nodeIndex, filterKey);
        return filter;
    }
}
