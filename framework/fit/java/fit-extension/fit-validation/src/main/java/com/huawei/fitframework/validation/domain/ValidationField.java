/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.validation.domain;

import com.huawei.fitframework.inspection.Validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 校验类型为字段的元数据类。
 *
 * @author 邬涨财
 * @since 2023-05-18
 */
public class ValidationField extends AbstractValidationMetadata {
    private final Field field;

    public ValidationField(Field field, Class<?>[] groups, Object value, Method validationMethod) {
        super(groups, value, validationMethod);
        this.field = Validation.notNull(field, "The field cannot be null when construct validation filed.");
    }

    @Override
    public AnnotatedElement element() {
        return this.field;
    }

    @Override
    public String name() {
        return this.field.getName();
    }

    @Override
    public Annotation[] annotations() {
        return this.field.getAnnotations();
    }
}
