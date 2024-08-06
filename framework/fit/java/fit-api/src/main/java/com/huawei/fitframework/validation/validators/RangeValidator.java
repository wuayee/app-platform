/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.validation.validators;

import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.validation.ConstraintValidator;
import com.huawei.fitframework.validation.constraints.Range;

/**
 * 表示 {@link Range} 约束的校验器。
 *
 * @author 邬涨财 w00575064
 * @since 2023-03-08
 */
public class RangeValidator implements ConstraintValidator<Range, Object> {
    private long min;
    private long max;

    @Override
    public void initialize(Range constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            return false;
        } else if (value instanceof Integer) {
            int convertedValue = ObjectUtils.cast(value);
            return convertedValue >= this.min && convertedValue <= this.max;
        } else if (value instanceof Long) {
            long convertedValue = ObjectUtils.cast(value);
            return convertedValue >= this.min && convertedValue <= this.max;
        } else if (value instanceof Float) {
            float convertedValue = ObjectUtils.cast(value);
            return convertedValue >= this.min && convertedValue <= this.max;
        } else if (value instanceof Double) {
            double convertedValue = ObjectUtils.cast(value);
            return convertedValue >= this.min && convertedValue <= this.max;
        } else {
            throw new UnsupportedOperationException("Failed to validate value: invalid value.");
        }
    }
}
