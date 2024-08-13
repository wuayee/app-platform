/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.validation.domain;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.validation.ValidationMetadata;

import java.lang.reflect.Method;

/**
 * 表示 {@link ValidationMetadata} 的抽象实现。
 *
 * @author 白鹏坤
 * @author 邬涨财
 * @since 2023-04-23
 */
public abstract class AbstractValidationMetadata implements ValidationMetadata {
    private final Class<?>[] groups;
    private final Object value;
    private final Method validationMethod;

    public AbstractValidationMetadata(Class<?>[] groups, Object value, Method validationMethod) {
        this.groups = Validation.notNull(groups, "The groups cannot be null when construct validation metadata.");
        this.value = value;
        this.validationMethod = Validation.notNull(validationMethod,
                "The validation method cannot be null when construct validation metadata.");
    }

    @Override
    public Class<?>[] groups() {
        return this.groups;
    }

    @Override
    public Object value() {
        return this.value;
    }

    @Override
    public Method getValidationMethod() {
        return this.validationMethod;
    }
}
