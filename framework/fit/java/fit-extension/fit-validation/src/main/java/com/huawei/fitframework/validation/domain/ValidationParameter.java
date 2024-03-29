/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.validation.domain;

import com.huawei.fitframework.inspection.Validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 校验类型为参数的元数据类。
 *
 * @author 邬涨财 w00575064
 * @since 2023-05-19
 */
public class ValidationParameter extends AbstractValidationMetadata {
    private final Parameter parameter;

    public ValidationParameter(Parameter parameter, Class<?>[] groups, Object value, Method validationMethod) {
        super(groups, value, validationMethod);
        this.parameter =
                Validation.notNull(parameter, "The parameter cannot be null when construct validation parameter.");
    }

    @Override
    public AnnotatedElement element() {
        return this.parameter;
    }

    @Override
    public String name() {
        return this.parameter.getName();
    }

    @Override
    public Annotation[] annotations() {
        return this.parameter.getAnnotations();
    }
}
