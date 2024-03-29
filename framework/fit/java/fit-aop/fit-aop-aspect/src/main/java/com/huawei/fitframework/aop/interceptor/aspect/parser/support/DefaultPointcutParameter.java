/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.aspect.parser.support;

import com.huawei.fitframework.aop.interceptor.aspect.parser.PointcutParameter;

/**
 * {@link PointcutParameter} 默认实现。
 *
 * @author 郭龙飞 gwx900499
 * @since 2023-03-08
 */
public class DefaultPointcutParameter implements PointcutParameter {
    private final String name;
    private final Class<?> type;
    private Object binding;

    public DefaultPointcutParameter(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Class<?> getType() {
        return this.type;
    }

    @Override
    public Object getBinding() {
        return this.binding;
    }

    @Override
    public void setBinding(Object boundValue) {
        this.binding = boundValue;
    }
}
