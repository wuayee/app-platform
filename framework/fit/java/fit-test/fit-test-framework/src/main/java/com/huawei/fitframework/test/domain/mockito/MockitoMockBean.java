/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.test.domain.mockito;

import com.huawei.fitframework.test.domain.resolver.MockBean;

import org.mockito.Mockito;

import java.lang.reflect.Field;

/**
 * Mockito 的 BeanMock 工具类。
 *
 * @author 邬涨财 w00575064
 * @since 2023-01-31
 */
public class MockitoMockBean implements MockBean {
    /**
     * 根据字段获得实例对象。
     *
     * @param field 表示字段的 {@link Field}。
     * @return 表示实例对象的 {@link Object}。
     */
    @Override
    public Object getBean(Field field) {
        return Mockito.mock(field.getType());
    }
}
