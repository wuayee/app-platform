/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fitframework.aop.ProceedingJoinPoint;
import com.huawei.fitframework.aop.interceptor.aspect.interceptor.AspectAfterInterceptor;
import com.huawei.fitframework.ioc.BeanFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * {@link AspectAfterInterceptor} 单元测试。
 *
 * @author 季聿阶
 * @since 2022-05-23
 */
@DisplayName("测试 AspectAfterInterceptor")
public class AspectAfterInterceptorTest {
    @Test
    @DisplayName("当 @After 定义的切面第一个参数是 ProceedingJoinPoint 时，生成方法拦截器失败")
    void givenAfterMethodWithProceedingJoinPointAsThe1stParamThenFailToGenerateMethodInterceptor()
            throws NoSuchMethodException {
        BeanFactory factory = mock(BeanFactory.class);
        Object target = new AspectAfterInterceptorTest();
        when(factory.get()).thenReturn(target);
        Method method = AspectAfterInterceptorTest.class.getDeclaredMethod("afterWithProceedingJoinPoint",
                ProceedingJoinPoint.class);
        IllegalArgumentException exception = catchThrowableOfType(
                () -> new AspectAfterInterceptor(factory, method),
                IllegalArgumentException.class);
        assertThat(exception).hasMessage(
                "The 1st parameter of @After interceptor in Aspect cannot be ProceedingJoinPoint.");
    }

    void afterWithProceedingJoinPoint(@SuppressWarnings("unused") ProceedingJoinPoint joinPoint) {}
}
