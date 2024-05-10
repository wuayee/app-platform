/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.common.validation;

import com.huawei.fitframework.validation.ConstraintValidator;

/**
 * The PatternValidator
 *
 * @author l00611472
 * @since 2023-12-14
 */
public class PatternValidator implements ConstraintValidator<Pattern, String> {
    private String regexp;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Pattern constraintAnnotation) {
        this.regexp = constraintAnnotation.regexp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(String value) {
        return value != null && value.matches(regexp);
    }
}
