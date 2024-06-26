/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.validators.rules.filters;

import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_INVALID;

import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.filters.FlowFilter;
import com.huawei.fitframework.inspection.Validation;

import java.util.Objects;

/**
 * 最小Size过滤器校验规则
 *
 * @author g00564732
 * @since 2023/09/25
 */
public class ThresholdFilterRule implements FilterRule {
    private static final String NUMBER_REGEX = "\\d+";

    private static final String THRESHOLD = "threshold";

    /**
     * 校验不同过滤器的合法性
     * 当校验不通过时，抛出运行时异常{@link JobberParamException}
     *
     * @param flowFilter 流程过滤器
     */
    @Override
    public void apply(FlowFilter flowFilter) {
        Validation.notNull(flowFilter.getFilterType(), exception("flow filter type"));
        String thresholdValue = flowFilter.getProperties().get(THRESHOLD);
        if (Objects.isNull(thresholdValue) || !isNumeric(thresholdValue)) {
            throw new JobberParamException(INPUT_PARAM_IS_INVALID, "flow filter threshold");
        }
    }

    private boolean isNumeric(String threshold) {
        return threshold.matches(NUMBER_REGEX);
    }
}
