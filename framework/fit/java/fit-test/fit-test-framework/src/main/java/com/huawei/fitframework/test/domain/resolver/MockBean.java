/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.test.domain.resolver;

import com.huawei.fitframework.annotation.Genericable;

import java.lang.reflect.Field;

/**
 * 为 Bean 的模拟构建提供工具方法。
 *
 * @author 邬涨财
 * @since 2023-01-31
 */
@Genericable
public interface MockBean {
    /**
     * 根据字段获得实例对象。
     *
     * @param field 表示字段的 {@link Field}。
     * @return 表示实例对象的 {@link Object}。
     */
    Object getBean(Field field);
}