/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.common.validation;

import modelengine.fitframework.validation.ConstraintValidator;

/**
 * The SizeValidator
 *
 * @author 刘信宏
 * @since 2023-12-14
 */
public class SizeValidator implements ConstraintValidator<Size, String> {
    private int min;

    private int max;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Size constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(String value) {
        return value != null && value.length() >= min && value.length() <= max;
    }
}
