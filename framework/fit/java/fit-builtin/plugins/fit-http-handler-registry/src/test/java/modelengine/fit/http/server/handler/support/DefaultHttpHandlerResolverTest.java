/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package modelengine.fit.http.server.handler.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.server.HttpClassicServer;
import modelengine.fit.http.server.HttpHandlerGroup;
import modelengine.fit.http.server.HttpServerFilter;
import modelengine.fit.http.server.handler.HttpResponseStatusResolver;
import modelengine.fit.http.server.handler.PropertyValueMapperResolver;
import modelengine.fit.http.server.handler.PropertyValueMetadataResolver;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.ioc.annotation.support.EmptyAnnotationMetadata;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.util.TypeUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 表示 {@link DefaultHttpHandlerResolver} 的单元测试。
 *
 * @author 杭潇
 * @since 2023-02-23
 */
@DisplayName("测试 DefaultHttpHandlerResolver 类")
public class DefaultHttpHandlerResolverTest {
    private DefaultHttpHandlerResolver defaultHttpHandlerResolver;
    private AnnotationMetadataResolver annotationMetadataResolver;

    @BeforeEach
    void setup() {
        HttpClassicServer httpServer = mock(HttpClassicServer.class);
        BeanContainer container = mock(BeanContainer.class);
        FitRuntime fitRuntime = mock(FitRuntime.class);
        this.annotationMetadataResolver = mock(AnnotationMetadataResolver.class);
        when(fitRuntime.resolverOfAnnotations()).thenReturn(this.annotationMetadataResolver);
        when(container.runtime()).thenReturn(fitRuntime);
        this.defaultHttpHandlerResolver = new DefaultHttpHandlerResolver(httpServer, container);
    }

    @Nested
    @DisplayName("测试 resolve() 方法")
    class TestResolve {
        @Test
        @DisplayName("给定合理的参数，返回值不为空")
        void givenValidParametersThenReturnIsNotEmpty() {
            BeanFactory beanFactory = this.initializeBeanFactory();

            List<HttpServerFilter> preFilters = new ArrayList<>();
            HttpServerFilter httpServerFilter = mock(HttpServerFilter.class);
            preFilters.add(httpServerFilter);

            PropertyValueMapperResolver mapperResolver = mock(PropertyValueMapperResolver.class);
            PropertyValueMetadataResolver metadataResolver = mock(PropertyValueMetadataResolver.class);
            HttpResponseStatusResolver responseStatusResolver = mock(HttpResponseStatusResolver.class);
            Optional<HttpHandlerGroup> resolve = DefaultHttpHandlerResolverTest.this.defaultHttpHandlerResolver.resolve(
                    beanFactory,
                    preFilters,
                    Optional::empty,
                    mapperResolver,
                    metadataResolver,
                    responseStatusResolver);
            assertThat(resolve).isNotEmpty();
        }

        private BeanFactory initializeBeanFactory() {
            BeanFactory candidate = mock(BeanFactory.class);
            when(candidate.get()).thenReturn("testsCandidate");
            BeanMetadata beanMetadata = mock(BeanMetadata.class);
            when(beanMetadata.name()).thenReturn("mock");
            when(candidate.metadata()).thenReturn(beanMetadata);
            Type type = Integer.class.getGenericSuperclass();
            AnnotationMetadata mock = EmptyAnnotationMetadata.INSTANCE;
            AnnotationMetadata annotationMetadata = mock(mock.getClass());
            when(annotationMetadata.isAnnotationPresent(RequestMapping.class)).thenReturn(true);
            RequestMapping requestMapping = mock(RequestMapping.class);
            when(annotationMetadata.getAnnotation(RequestMapping.class)).thenReturn(requestMapping);
            String[] testPath = {"testResolve/main,testResolve/modify/main,testResolve/helloWord"};
            when(requestMapping.path()).thenReturn(testPath);
            when(DefaultHttpHandlerResolverTest.this.annotationMetadataResolver.resolve(TypeUtils.toClass(type)))
                    .thenReturn(annotationMetadata);
            when(beanMetadata.type()).thenReturn(type);
            Method[] methods = TypeUtils.toClass(candidate.metadata().type()).getDeclaredMethods();
            for (int i = methods.length - 1; i >= 0; i--) {
                when(DefaultHttpHandlerResolverTest.this.annotationMetadataResolver
                        .resolve(TypeUtils.toClass(candidate.metadata().type()).getDeclaredMethods()[i]))
                        .thenReturn(annotationMetadata);
            }
            return candidate;
        }
    }
}
