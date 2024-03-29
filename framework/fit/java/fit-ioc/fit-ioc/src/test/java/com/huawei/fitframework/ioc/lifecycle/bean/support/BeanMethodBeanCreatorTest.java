/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.ioc.lifecycle.bean.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanCreationException;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.ioc.BeanMetadata;
import com.huawei.fitframework.ioc.BeanResolver;
import com.huawei.fitframework.ioc.lifecycle.bean.BeanCreator;
import com.huawei.fitframework.ioc.lifecycle.bean.BeanCreators;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.runtime.FitRuntime;
import com.huawei.fitframework.util.ReflectionUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * {@link BeanMethodBeanCreator} 的单元测试。
 *
 * @author bWX1068551
 * @since 2023-03-02
 */
@DisplayName("测试 BeanMethodBeanCreator 类")
class BeanMethodBeanCreatorTest {
    private BeanMetadata metadata;
    private Method method;

    @SuppressWarnings("unused")
    private String testMethod(String value) {
        return value;
    }

    @BeforeEach
    void setup() {
        this.method = ReflectionUtils.getDeclaredMethod(this.getClass(), "testMethod", String.class);

        BeanResolver resolver = mock(BeanResolver.class);
        FitRuntime runtime = mock(FitRuntime.class);
        when(runtime.resolverOfBeans()).thenReturn(resolver);

        Plugin plugin = mock(Plugin.class);
        when(plugin.runtime()).thenReturn(runtime);

        final BeanFactory beanFactory = mock(BeanFactory.class);
        when(beanFactory.get()).thenReturn(this);

        BeanContainer container = mock(BeanContainer.class);
        when(container.plugin()).thenReturn(plugin);
        when(container.runtime()).thenCallRealMethod();
        when(container.factory(anyString())).thenReturn(Optional.of(beanFactory));

        this.metadata = mock(BeanMetadata.class);
        when(this.metadata.container()).thenReturn(container);
        when(this.metadata.runtime()).thenCallRealMethod();
        when(this.metadata.name()).thenReturn("testMethod");
    }

    @Test
    @DisplayName("当 BeanDefinition 为 null 时，抛出异常")
    void shouldThrowWhenBeanDefinitionIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new BeanMethodBeanCreator(null, this.method));
    }

    @Test
    @DisplayName("当 Method 为 null 时，抛出异常")
    void shouldThrowWhenMethodIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new BeanMethodBeanCreator(this.metadata, null));
    }

    @Test
    @DisplayName("使用参数数量不匹配时，抛出异常")
    void shouldThrowWhenTheNumberOfArgumentsNotMatch() {
        final BeanCreator beanCreator = BeanCreators.byMethod(this.metadata, this.method);
        assertThatThrownBy(() -> beanCreator.create(new Object[] {1, "hello"})).isInstanceOf(
                BeanCreationException.class);
    }

    @Test
    @DisplayName("使用正确的参数可成功实例化Bean")
    void shouldReturnBeanCreatedByMethod() {
        final BeanMethodBeanCreator beanCreator = new BeanMethodBeanCreator(this.metadata, this.method);
        final Object obj = beanCreator.create(new Object[] {"hello"});
        assertThat(obj).isInstanceOf(String.class).isEqualTo("hello");
    }
}
