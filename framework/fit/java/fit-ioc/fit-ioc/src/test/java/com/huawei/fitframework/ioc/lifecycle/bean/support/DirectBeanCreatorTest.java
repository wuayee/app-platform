/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.ioc.lifecycle.bean.support;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.huawei.fitframework.ioc.BeanCreationException;
import com.huawei.fitframework.ioc.lifecycle.bean.BeanCreator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("测试 DirectBeanCreator 类")
class DirectBeanCreatorTest {
    @Test
    @DisplayName("当Bean为 null 时抛出异常")
    void should_throw_when_direct_bean_is_null() {
        assertThrows(IllegalArgumentException.class, () -> new DirectBeanCreator(null));
    }

    @Test
    @DisplayName("当使用初始化参数来实例化Bean时抛出异常")
    void should_throw_when_create_bean_with_arguments() {
        Object bean = new byte[0];
        BeanCreator creator = new DirectBeanCreator(bean);
        assertThrows(BeanCreationException.class, () -> creator.create(new Object[] {1, "hello"}));
    }

    @Test
    @DisplayName("当创建Bean时直接返回Bean实例")
    void should_return_bean_directly() {
        Object bean = new byte[0];
        BeanCreator creator = new DirectBeanCreator(bean);
        Object result = creator.create(new Object[0]);
        assertSame(bean, result);
    }
}
