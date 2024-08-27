/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fitframework.validation.validators;

import modelengine.fitframework.validation.ConstraintValidator;
import modelengine.fitframework.validation.constraints.Positive;

/**
 * 表示 {@link Positive} 约束的校验器。
 *
 * @author 吕博文
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
