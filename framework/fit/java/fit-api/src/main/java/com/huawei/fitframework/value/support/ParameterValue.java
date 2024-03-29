/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.value.support;

import com.huawei.fitframework.value.PropertyValue;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * 表示参数类型的属性值。
 *
 * @author 邬涨财 w00575064
 * @since 2023-11-14
 */
public class ParameterValue implements PropertyValue {
    private final Parameter parameter;

    public ParameterValue(Parameter parameter) {
        this.parameter = parameter;
    }

    @Override
    public Class<?> getType() {
        return this.parameter.getType();
    }

    @Override
    public Type getParameterizedType() {
        return this.parameter.getParameterizedType();
    }

    @Override
    public AnnotatedElement getElement() {
        return this.parameter;
    }

    @Override
    public String getName() {
        return this.parameter.getName();
    }
}
