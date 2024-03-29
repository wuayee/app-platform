/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.ioc.annotation.tree;

import com.huawei.fitframework.util.convert.Converter;

/**
 * 为属性值的转换程序提供缓存。
 *
 * @author 梁济时 l00815032
 * @since 2023-01-28
 */
public interface ConverterCache {
    /**
     * 获取指定类型的转换程序。
     *
     * @param converterClass 表示转换程序的类型的 {@link Class}。
     * @return 表示该类型的转换程序的 {@link Converter}。
     */
    Converter get(Class<? extends Converter> converterClass);
}
