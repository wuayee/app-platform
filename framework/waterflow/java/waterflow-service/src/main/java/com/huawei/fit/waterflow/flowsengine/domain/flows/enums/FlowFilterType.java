/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.enums;

import static com.huawei.fit.jober.common.ErrorCodes.ENUM_CONVERT_FAILED;
import static java.util.Locale.ROOT;

import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.nodes.filters.BatchSizeFilterParser;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.nodes.filters.FilterParser;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.nodes.filters.TransBatchSizeFilterParser;
import com.huawei.fit.waterflow.flowsengine.domain.flows.validators.rules.filters.BatchSizeFilterRule;
import com.huawei.fit.waterflow.flowsengine.domain.flows.validators.rules.filters.FilterRule;
import com.huawei.fit.waterflow.flowsengine.domain.flows.validators.rules.filters.TransBatchSizeFilterRule;

import lombok.Getter;

import java.util.Arrays;

/**
 * 流程定义过滤器类型
 *
 * @author 高诗意
 * @since 2023/08/14
 */
@Getter
public enum FlowFilterType {
    MINIMUM_SIZE_FILTER("MINIMUM_SIZE_FILTER", new BatchSizeFilterParser(), new BatchSizeFilterRule()),
    BATCH_FILTER("BATCH_FILTER", new BatchSizeFilterParser(), new BatchSizeFilterRule()),
    TRANS_BATCH_FILTER("SAME_BATCH_FILTER", new TransBatchSizeFilterParser(), new TransBatchSizeFilterRule());

    private final String code;

    private final FilterParser filterParser;

    private final FilterRule filterRule;

    FlowFilterType(String code, FilterParser filterParser, FilterRule filterRule) {
        this.code = code;
        this.filterParser = filterParser;
        this.filterRule = filterRule;
    }

    /**
     * getFilterType
     *
     * @param code code
     * @return FlowFilterType
     */
    public static FlowFilterType getFilterType(String code) {
        return Arrays.stream(values())
                .filter(value -> value.getCode().equals(code.toUpperCase(ROOT)))
                .findFirst()
                .orElseThrow(() -> new JobberParamException(ENUM_CONVERT_FAILED, "FlowFilter", code));
    }
}
