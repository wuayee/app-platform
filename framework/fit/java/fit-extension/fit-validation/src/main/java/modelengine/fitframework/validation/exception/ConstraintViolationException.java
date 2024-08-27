/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation.exception;

import modelengine.fitframework.validation.ConstraintViolation;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 表示校验失败的异常。
 *
 * @author 邬涨财
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
