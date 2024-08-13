/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.http.server.handler.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.http.annotation.ExceptionHandler;
import com.huawei.fit.http.annotation.ResponseStatus;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.http.server.HttpServerResponseException;
import com.huawei.fitframework.annotation.Scope;
import com.huawei.fitframework.exception.MethodInvocationException;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.ioc.BeanMetadata;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolver;
import com.huawei.fitframework.merge.ConflictException;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.runtime.FitRuntime;
import com.huawei.fitframework.util.ReflectionUtils;
import com.huawei.fitframework.util.TypeUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 表示 {@link DefaultExceptionHandlerRegistry} 的单元测试。
 *
 * @author 杭潇
 * @since 2023-02-27
 */
@DisplayName("测试 HttpExceptionHandlerResolver 类")
public class ExceptionHandlerSupplierTest {
    private DefaultExceptionHandlerRegistry exceptionHandlerResolver;
    private BeanFactory beanFactory;
    private BeanContainer beanContainer;
    private final Type type = Integer.class.getGenericSuperclass();

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setup() {
        this.initializeBeanFactory();
        Plugin plugin = mock(Plugin.class);
        BeanContainer mockedBeanContainer = mock(BeanContainer.class);
        when(plugin.container()).thenReturn(mockedBeanContainer);
        FitRuntime fitRuntime = mock(FitRuntime.class);
        when(mockedBeanContainer.runtime()).thenReturn(fitRuntime);
        AnnotationMetadataResolver annotationMetadataResolver = mock(AnnotationMetadataResolver.class);
        when(fitRuntime.resolverOfAnnotations()).thenReturn(annotationMetadataResolver);
        AnnotationMetadata annotationMetadata = mock(AnnotationMetadata.class);

        Method[] declaredMethod = TypeUtils.toClass(this.type).getDeclaredMethods();
        for (Method method : declaredMethod) {
            when(annotationMetadataResolver.resolve(method)).thenReturn(annotationMetadata);
        }
        when(annotationMetadata.isAnnotationPresent(ExceptionHandler.class)).thenReturn(true);
        when(annotationMetadata.isAnnotationPresent(ResponseStatus.class)).thenReturn(true);
        ExceptionHandler exceptionHandler = mock(ExceptionHandler.class);
        ResponseStatus responseStatus = mock(ResponseStatus.class);
        when(annotationMetadata.getAnnotation(ExceptionHandler.class)).thenReturn(exceptionHandler);
        when(annotationMetadata.getAnnotation(ResponseStatus.class)).thenReturn(responseStatus);
        Class<? extends Throwable>[] clazz = new Class[] {HttpServerResponseException.class};
        when(exceptionHandler.value()).thenReturn(clazz);
        when(exceptionHandler.scope()).thenReturn(Scope.PLUGIN);
        when(responseStatus.code()).thenReturn(HttpResponseStatus.OK);

        List<BeanFactory> beanFactoryList = new ArrayList<>();
        beanFactoryList.add(this.beanFactory);
        when(mockedBeanContainer.factories()).thenReturn(beanFactoryList);
        when(mockedBeanContainer.all()).thenReturn(beanFactoryList);
        this.beanContainer = mockedBeanContainer;
        this.exceptionHandlerResolver = new DefaultExceptionHandlerRegistry(mockedBeanContainer);
    }

    private void initializeBeanFactory() {
        this.beanFactory = mock(BeanFactory.class);
        BeanMetadata beanMetadata = mock(BeanMetadata.class);
        when(this.beanFactory.metadata()).thenReturn(beanMetadata);
        when(beanMetadata.type()).thenReturn(this.type);
        when(this.beanFactory.get()).thenReturn("beanFactoryTarget");
    }

    @Nested
    @DisplayName("测试 resolveMethod() 方法")
    class TestResolveMethod {
        @Test
        @DisplayName("给定不可解的参数，抛出异常")
        void givenCanNotResolveParametersThenThrowException() {
            Method method = ReflectionUtils.getDeclaredMethod(exceptionHandlerResolver.getClass(),
                    "resolve",
                    BeanContainer.class,
                    BeanFactory.class);
            MethodInvocationException methodInvocationException = catchThrowableOfType(() -> ReflectionUtils.invoke(
                    exceptionHandlerResolver,
                    method,
                    beanContainer,
                    beanFactory), MethodInvocationException.class);
            assertThat(methodInvocationException.getCause() instanceof ConflictException).isTrue();
            assertThat(methodInvocationException.getCause()
                    .getMessage()
                    .startsWith("Conflict in merge map process.")).isTrue();
        }
    }
}
