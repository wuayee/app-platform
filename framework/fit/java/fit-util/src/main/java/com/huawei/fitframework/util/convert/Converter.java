/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.util.convert;

import java.util.function.Function;

/**
 * 为转换提供方法。
 *
 * @author 梁济时
 * @since 2023-01-28
 */
@FunctionalInterface
public interface Converter extends Function<Object, Object> {
    /**
     * 将原始对象进行转换，得到目标对象。
     *
     * @param value 表示待转换的原始对象的 {@link Object}。
     * @return 表示转换后的对象的 {@link Object}。
     */
    Object convert(Object value);

    @Override
    default Object apply(Object value) {
        return this.convert(value);
    }
}
