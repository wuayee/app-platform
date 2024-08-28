/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.filters;

import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.filters.FlowBatchSizeFilter;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.filters.FlowFilter;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowFilterType;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.FlowGraphData;

/**
 * ClassName
 * 简述
 *
 * @author 高诗意
 * @since 2023/09/25
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
