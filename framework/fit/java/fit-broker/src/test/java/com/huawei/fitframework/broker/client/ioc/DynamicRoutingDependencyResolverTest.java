/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.broker.client.ioc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fitframework.annotation.Genericable;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.ioc.BeanMetadata;
import com.huawei.fitframework.ioc.DependencyResolvingResult;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.ioc.annotation.support.DefaultAnnotationMetadataResolver;
import com.huawei.fitframework.runtime.FitRuntime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 表示 {@link DynamicRoutingDependencyResolver} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-07-17
 */
@DisplayName("测试 DynamicRoutingDependencyResolver")
public class DynamicRoutingDependencyResolverTest {
    private BeanMetadata metadata;
    private BeanFactory factory;
    private DynamicRoutingDependencyResolver resolver;

    @BeforeEach
    void setup() {
        this.metadata = mock(BeanMetadata.class);
        BeanContainer container = mock(BeanContainer.class);
        when(this.metadata.container()).thenReturn(container);
        this.factory = mock(BeanFactory.class);
        when(container.lookup(anyString())).thenReturn(Optional.of(this.factory));
        FitRuntime runtime = mock(FitRuntime.class);
        when(this.metadata.runtime()).thenReturn(runtime);
        when(runtime.resolverOfAnnotations()).thenReturn(new DefaultAnnotationMetadataResolver());
        this.resolver = new DynamicRoutingDependencyResolver();
    }

    @AfterEach
    void teardown() {
        this.resolver = null;
        this.factory = null;
        this.metadata = null;
    }

    @Test
    @DisplayName("当所依赖的 Bean 的类型为 List 时，返回原始解析结果")
    void shouldReturnOriginalResultWhenTypeIsList() {
        List<?> expected = new ArrayList<>();
        AnnotationMetadata annotations = AnnotationMetadata.empty();
        when(this.factory.get()).thenReturn(expected);
        DependencyResolvingResult actual = this.resolver.resolve(this.metadata, "name", List.class, annotations);
        assertThat(actual).isNotNull();
        assertThat(actual.get()).isEqualTo(expected);
    }

    @Test
    @DisplayName("当所依赖的 Bean 的类型为 Map 时，返回原始解析结果")
    void shouldReturnOriginalResultWhenTypeIsMap() {
        Map<String, ?> expected = new HashMap<>();
        AnnotationMetadata annotations = AnnotationMetadata.empty();
        when(this.factory.get()).thenReturn(expected);
        DependencyResolvingResult actual = this.resolver.resolve(this.metadata, "name", Map.class, annotations);
        assertThat(actual).isNotNull();
        assertThat(actual.get()).isEqualTo(expected);
    }

    @Test
    @DisplayName("当所依赖的 Bean 的类型不是接口时，返回原始解析结果")
    void shouldReturnOriginalResultWhenTypeIsNotInterface() {
        Object expected = new Object();
        AnnotationMetadata annotations = AnnotationMetadata.empty();
        when(this.factory.get()).thenReturn(expected);
        DependencyResolvingResult actual = this.resolver.resolve(this.metadata, "name", Object.class, annotations);
        assertThat(actual).isNotNull();
        assertThat(actual.get()).isEqualTo(expected);
    }

    @Test
    @DisplayName("当所依赖的接口不包含 Genericable 时，返回原始解析结果")
    void shouldReturnOriginalResultWhenInterfaceHasNoGenericable() {
        Object expected = new Object();
        AnnotationMetadata annotations = AnnotationMetadata.empty();
        when(this.factory.get()).thenReturn(expected);
        DependencyResolvingResult actual =
                this.resolver.resolve(this.metadata, "name", TestInterfaceWithoutGenericable.class, annotations);
        assertThat(actual).isNotNull();
        assertThat(actual.get()).isEqualTo(expected);
    }

    @Test
    @DisplayName("当所依赖的接口包含 Genericable 时，返回动态代理")
    void shouldReturnDynamicProxyWhenInterfaceHasGenericable() {
        AnnotationMetadata annotations = AnnotationMetadata.empty();
        DependencyResolvingResult actual =
                this.resolver.resolve(this.metadata, "name", TestInterfaceWithGenericable.class, annotations);
        assertThat(actual).isNotNull().returns(true, DependencyResolvingResult::resolved);
        assertThat(actual.get()).isNotNull();
    }

    interface TestInterfaceWithoutGenericable {
        void func1();
    }

    interface TestInterfaceWithGenericable {
        @Genericable(id = "g1")
        void func1();
    }
}
