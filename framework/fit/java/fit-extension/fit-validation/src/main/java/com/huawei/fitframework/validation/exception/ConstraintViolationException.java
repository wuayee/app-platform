/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.validation.exception;

import com.huawei.fitframework.validation.ConstraintViolation;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 表示校验失败的异常。
 *
 * @author 邬涨财 w00575064
 * @since 2023-05-18
 */
public class ConstraintViolationException extends RuntimeException {
    public ConstraintViolationException(String message) {
        super(message);
    }

    public ConstraintViolationException(List<ConstraintViolation> constraintViolations) {
        this(constraintViolations != null ? buildMessage(constraintViolations) : null);
    }

    private static String buildMessage(List<ConstraintViolation> constraintViolations) {
        return constraintViolations.stream()
                .filter(Objects::nonNull)
                .map(violation -> getPath(violation) + ": " + violation.message())
                .collect(Collectors.joining(", "));
    }

    private static Object getPath(ConstraintViolation violation) {
        return violation.validationMethod().getName() + "." + violation.propertyName();
    }
}
