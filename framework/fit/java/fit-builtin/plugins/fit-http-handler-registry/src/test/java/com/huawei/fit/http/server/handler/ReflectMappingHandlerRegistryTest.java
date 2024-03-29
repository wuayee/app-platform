/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.http.server.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.http.server.HttpClassicServer;
import com.huawei.fit.http.server.HttpDispatcher;
import com.huawei.fit.http.server.HttpServerFilter;
import com.huawei.fit.http.server.HttpServerFilterSupplier;
import com.huawei.fit.http.server.handler.support.DefaultHttpHandlerResolver;
import com.huawei.fitframework.conf.Config;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.ioc.BeanMetadata;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolver;
import com.huawei.fitframework.ioc.annotation.support.EmptyAnnotationMetadata;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.runtime.FitRuntime;
import com.huawei.fitframework.util.TypeUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 表示 {@link ReflectMappingHandlerRegistry} 的单元测试。
 *
 * @author 杭潇 h00675922
 * @since 2023-02-23
 */
@DisplayName("测试 ReflectMappingHandlerRegistry 类")
public class ReflectMappingHandlerRegistryTest {
    private ReflectMappingHandlerRegistry registry;
    private BeanContainer container;
    private Type type;
    private AnnotationMetadataResolver resolver;
    private AnnotationMetadata metadata;
    private Plugin plugin;

    @BeforeEach
    void setup() {
        this.initializeBeanContainer();
        this.initializeBeanFactory();
        this.initializeAnnotationMetadata();

        HttpClassicServer httpServer = mock(HttpClassicServer.class);
        HttpDispatcher httpDispatcher = mock(HttpDispatcher.class);
        when(httpServer.dispatcher()).thenReturn(httpDispatcher);

        HttpHandlerResolver handlerResolver = new DefaultHttpHandlerResolver(httpServer, this.container);
        ExceptionHandlerRegistry exceptionHandlerSupplier = mock(ExceptionHandlerRegistry.class);
        when(exceptionHandlerSupplier.addExceptionHandlers(any())).thenReturn(Collections.emptyMap());
        this.registry = new ReflectMappingHandlerRegistry(this.container,
                handlerResolver,
                httpServer,
                exceptionHandlerSupplier);
    }

    private void initializeBeanFactory() {
        BeanFactory beanFactoryResponseStatus = mock(BeanFactory.class);
        when(this.container.factory(HttpResponseStatusResolverSupplier.class)).thenReturn(Optional.of(
                beanFactoryResponseStatus));
        HttpResponseStatusResolverSupplier httpResponseStatusResolverSupplier =
                mock(HttpResponseStatusResolverSupplier.class);
        when(beanFactoryResponseStatus.get()).thenReturn(httpResponseStatusResolverSupplier);
        HttpResponseStatusResolver httpResponseStatusResolver = mock(HttpResponseStatusResolver.class);
        when(httpResponseStatusResolverSupplier.get(this.container)).thenReturn(httpResponseStatusResolver);

        BeanFactory beanFactoryHttpServerFilterSupplier = mock(BeanFactory.class);
        when(this.container.factory(HttpServerFilterSupplier.class)).thenReturn(Optional.of(
                beanFactoryHttpServerFilterSupplier));
        HttpServerFilterSupplier defaultHttpServerFilterSupplier = new DefaultHttpServerFilterSupplier();
        HttpServerFilterSupplier httpServerFilterSupplier = mock(defaultHttpServerFilterSupplier.getClass());
        when(beanFactoryHttpServerFilterSupplier.get()).thenReturn(httpServerFilterSupplier);
        List<HttpServerFilter> httpServerFilterList = new ArrayList<>();
        HttpServerFilter httpServerFilter = mock(HttpServerFilter.class);
        httpServerFilterList.add(httpServerFilter);
        when(httpServerFilterSupplier.get(this.container)).thenReturn(httpServerFilterList);

        BeanFactory beanFactoryParameterMapper = mock(BeanFactory.class);
        when(this.container.factory(PropertyValueMapperResolverSupplier.class)).thenReturn(Optional.of(
                beanFactoryParameterMapper));
        PropertyValueMapperResolverSupplier parameterMapperResolverSupplier =
                mock(PropertyValueMapperResolverSupplier.class);
        when(beanFactoryParameterMapper.get()).thenReturn(parameterMapperResolverSupplier);
        PropertyValueMapperResolver parameterMapperResolver = mock(PropertyValueMapperResolver.class);
        when(parameterMapperResolverSupplier.get(this.container)).thenReturn(parameterMapperResolver);
    }

    private void initializeAnnotationMetadata() {
        AnnotationMetadata mock = EmptyAnnotationMetadata.INSTANCE;
        this.metadata = mock(mock.getClass());
        when(this.metadata.isAnnotationPresent(RequestMapping.class)).thenReturn(true);
        RequestMapping requestMapping = mock(RequestMapping.class);
        HttpRequestMethod[] httpRequestMethods =
                new HttpRequestMethod[] {HttpRequestMethod.GET, HttpRequestMethod.CONNECT};
        when(requestMapping.method()).thenReturn(httpRequestMethods);
        when(this.metadata.getAnnotation(RequestMapping.class)).thenReturn(requestMapping);
        String[] testPath = {"testResolve/main,testResolve/modify/main,testResolve/helloWord"};
        when(requestMapping.path()).thenReturn(testPath);

        this.type = Integer.class.getGenericSuperclass();
        when(this.resolver.resolve(TypeUtils.toClass(this.type))).thenReturn(this.metadata);
    }

    private void initializeBeanContainer() {
        this.container = mock(BeanContainer.class);
        this.plugin = mock(Plugin.class);
        when(this.plugin.container()).thenReturn(this.container);
        FitRuntime fitRuntime = mock(FitRuntime.class);
        this.resolver = mock(AnnotationMetadataResolver.class);
        when(fitRuntime.resolverOfAnnotations()).thenReturn(this.resolver);
        when(this.container.runtime()).thenReturn(fitRuntime);
        when(this.container.plugin()).thenReturn(this.plugin);
        Config config = mock(Config.class);
        when(this.plugin.config()).thenReturn(config);
        when(config.get("server.http.context-path", String.class)).thenReturn("/context");
    }

    @AfterEach
    void teardown() {
        this.plugin.close();
        this.container.close();
    }

    @Nested
    @DisplayName("测试 onPluginStarted() 方法")
    class TestOnPluginStartedMethod {
        @Test
        @DisplayName("注册成功，未抛出异常")
        void registrySuccessfullyWithoutException() {
            this.mockBeanFactoryData();
            assertDoesNotThrow(() -> ReflectMappingHandlerRegistryTest.this.registry.onPluginStarted(
                    ReflectMappingHandlerRegistryTest.this.plugin));
        }

        private void mockBeanFactoryData() {
            BeanFactory beanFactoryMethodName = mock(BeanFactory.class);
            when(this.container().factory(HttpMethodNameResolverSupplier.class)).thenReturn(Optional.of(
                    beanFactoryMethodName));
            HttpMethodNameResolverSupplier httpMethodNameResolverSupplier = mock(HttpMethodNameResolverSupplier.class);
            when(beanFactoryMethodName.get()).thenReturn(httpMethodNameResolverSupplier);
            HttpMethodNameResolver httpMethodNameResolver = mock(HttpMethodNameResolver.class);
            when(httpMethodNameResolverSupplier.get(ReflectMappingHandlerRegistryTest.this.container)).thenReturn(
                    httpMethodNameResolver);
            List<BeanFactory> beanFactoryList = new ArrayList<>();
            BeanMetadata beanMetadata = mock(BeanMetadata.class);
            when(beanMetadata.name()).thenReturn("mock");
            when(beanFactoryMethodName.metadata()).thenReturn(beanMetadata);
            when(beanMetadata.type()).thenReturn(ReflectMappingHandlerRegistryTest.this.type);

            beanFactoryList.add(beanFactoryMethodName);
            when(ReflectMappingHandlerRegistryTest.this.container.factories()).thenReturn(beanFactoryList);

            when(beanMetadata.type()).thenReturn(ReflectMappingHandlerRegistryTest.this.type);
            Method[] methods = TypeUtils.toClass(beanFactoryMethodName.metadata().type()).getDeclaredMethods();
            for (Method method : methods) {
                when(ReflectMappingHandlerRegistryTest.this.resolver.resolve(method)).thenReturn(
                        ReflectMappingHandlerRegistryTest.this.metadata);
            }
        }

        private BeanContainer container() {
            return ReflectMappingHandlerRegistryTest.this.container;
        }
    }
}
