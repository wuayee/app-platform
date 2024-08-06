/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.validation.validators;

import com.huawei.fitframework.validation.ConstraintValidator;
import com.huawei.fitframework.validation.constraints.Positive;

/**
 * 表示 {@link Positive} 约束的校验器。
 *
 * @author 吕博文 l50044051
 * @since 2024-07-29
 */
public class PositiveValidator implements ConstraintValidator<Positive, Number> {
    @Override
    public boolean isValid(Number value) {
        if (value == null) {
            return false;
        }
        return value.doubleValue() > 0;
    }
}
