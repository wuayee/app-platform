/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.validators.rules.filters;

import static com.huawei.fit.waterflow.common.ErrorCodes.INPUT_PARAM_IS_INVALID;

import com.huawei.fit.waterflow.common.exceptions.WaterflowParamException;
import com.huawei.fit.waterflow.domain.definitions.nodes.filters.FlowFilter;
import com.huawei.fitframework.inspection.Validation;

import java.util.Objects;

/**
 * 最小Size过滤器校验规则
 *
 * @author g00564732
 * @since 1.0
 */
public class ThresholdFilterRule implements FilterRule {
    private static final String NUMBER_REGEX = "\\d+";

    private static final String THRESHOLD = "threshold";

    /**
     * 校验不同过滤器的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowFilter 流程过滤器
     */
    @Override
    public void apply(FlowFilter flowFilter) {
        Validation.notNull(flowFilter.getFilterType(), exception("flow filter type"));
        String thresholdValue = flowFilter.getProperties().get(THRESHOLD);
        if (Objects.isNull(thresholdValue) || !isNumeric(thresholdValue)) {
            throw new WaterflowParamException(INPUT_PARAM_IS_INVALID, "flow filter threshold");
        }
    }

    private boolean isNumeric(String threshold) {
        return threshold.matches(NUMBER_REGEX);
    }
}
