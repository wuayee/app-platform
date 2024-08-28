/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.enums;

import static java.util.Locale.ROOT;
import static modelengine.fit.waterflow.common.ErrorCodes.ENUM_CONVERT_FAILED;

import lombok.Getter;
import modelengine.fit.waterflow.common.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.domain.parsers.nodes.filters.BatchSizeFilterParser;
import modelengine.fit.waterflow.domain.parsers.nodes.filters.FilterParser;
import modelengine.fit.waterflow.domain.parsers.nodes.filters.TransBatchSizeFilterParser;
import modelengine.fit.waterflow.domain.validators.rules.filters.BatchSizeFilterRule;
import modelengine.fit.waterflow.domain.validators.rules.filters.FilterRule;
import modelengine.fit.waterflow.domain.validators.rules.filters.TransBatchSizeFilterRule;

import java.util.Arrays;

/**
 * 流程定义过滤器类型
 *
 * @author 高诗意
 * @since 1.0
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
                .orElseThrow(() -> new WaterflowParamException(ENUM_CONVERT_FAILED, "FlowFilter", code));
    }
}
