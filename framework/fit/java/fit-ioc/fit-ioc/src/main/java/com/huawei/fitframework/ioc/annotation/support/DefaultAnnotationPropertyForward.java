/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.ioc.annotation.support;

import com.huawei.fitframework.ioc.annotation.AnnotationProperty;
import com.huawei.fitframework.ioc.annotation.AnnotationPropertyForward;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.util.convert.Converter;

/**
 * 为 {@link AnnotationPropertyForward} 提供默认实现。
 *
 * @author 梁济时
 * @since 2023-01-28
 */
public final class DefaultAnnotationPropertyForward implements AnnotationPropertyForward {
    private final AnnotationProperty target;
    private final Class<? extends Converter> converterClass;

    public DefaultAnnotationPropertyForward(AnnotationProperty target, Class<? extends Converter> converterClass) {
        this.target = target;
        this.converterClass = converterClass;
    }

    @Override
    public AnnotationProperty target() {
        return this.target;
    }

    @Override
    public Class<? extends Converter> converterClass() {
        return this.converterClass;
    }

    @Override
    public String toString() {
        return StringUtils.format("[target={0}, converter={1}]", this.target, this.converterClass.getName());
    }
}
