/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fitframework.aop.interceptor.aspect.interceptor.AspectAroundInterceptor;
import com.huawei.fitframework.ioc.BeanFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * {@link AspectAroundInterceptor} 单元测试。
 *
 * @author 季聿阶
 * @since 2022-05-23
 */
@DisplayName("测试 AspectAroundInterceptor")
public class AspectAroundInterceptorTest {
    @Test
    @DisplayName("当 @Around 定义的切面没有参数时，生成方法拦截器失败")
    void givenAroundMethodNoParametersThenFailToGenerateMethodInterceptor() throws NoSuchMethodException {
        Method method = AspectAroundInterceptorTest.class.getDeclaredMethod("aroundWithoutParameters");
        BeanFactory aspectFactory = mock(BeanFactory.class);
        Object aspect = new AspectAroundInterceptorTest();
        when(aspectFactory.get()).thenReturn(aspect);
        IllegalArgumentException exception =
                catchThrowableOfType(() -> new AspectAroundInterceptor(aspectFactory, method),
                        IllegalArgumentException.class);
        assertThat(exception).hasMessage(
                "@Around interceptor in Aspect must have at least 1 parameter: ProceedingJoinPoint.");
    }

    @Test
    @DisplayName("当 @Around 定义的切面缺少 ProceedingJoinPoint 参数时，生成方法拦截器失败")
    void givenAroundMethodNoProceedingJoinPointThenFailToGenerateMethodInterceptor() throws NoSuchMethodException {
        Method method =
                AspectAroundInterceptorTest.class.getDeclaredMethod("aroundWithoutProceedingJoinPoint", String.class);
        BeanFactory aspectFactory = mock(BeanFactory.class);
        Object aspect = new AspectAroundInterceptorTest();
        when(aspectFactory.get()).thenReturn(aspect);
        IllegalArgumentException exception =
                catchThrowableOfType(() -> new AspectAroundInterceptor(aspectFactory, method),
                        IllegalArgumentException.class);
        assertThat(exception).hasMessage(
                "The 1st parameter of @Around interceptor in Aspect must be ProceedingJoinPoint.");
    }

    Optional<Object> aroundWithoutParameters() {
        return Optional.empty();
    }

    Object aroundWithoutProceedingJoinPoint(String name) {
        return name;
    }
}
