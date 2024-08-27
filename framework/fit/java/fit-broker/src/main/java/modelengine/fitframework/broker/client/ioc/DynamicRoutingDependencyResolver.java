/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client.ioc;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Genericable;
import modelengine.fitframework.aop.interceptor.MethodInterceptor;
import modelengine.fitframework.aop.interceptor.MethodMatcher;
import modelengine.fitframework.aop.proxy.AopProxyFactory;
import modelengine.fitframework.aop.proxy.InterceptSupport;
import modelengine.fitframework.aop.proxy.support.DefaultInterceptSupport;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.aop.DynamicRoutingInterceptor;
import modelengine.fitframework.conf.runtime.CommunicationProtocol;
import modelengine.fitframework.conf.runtime.SerializationFormat;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.DependencyResolver;
import modelengine.fitframework.ioc.DependencyResolvingResult;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.ioc.support.DefaultDependencyResolver;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.TypeUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 支持动态路由的依赖解析器。
 *
 * @author 季聿阶
 * @since 2022-05-26
 */
public class DynamicRoutingDependencyResolver implements DependencyResolver {
    private final DependencyResolver resolver;

    public DynamicRoutingDependencyResolver() {
        this(new DefaultDependencyResolver());
    }

    /**
     * 使用一个依赖解析器来实例化 {@link DynamicRoutingDependencyResolver}。
     *
     * @param resolver 表示依赖解析器的 {@link DependencyResolver}。
     */
    public DynamicRoutingDependencyResolver(DependencyResolver resolver) {
        this.resolver = notNull(resolver, "The dependency resolver cannot be null.");
    }

    @Override
    public DependencyResolvingResult resolve(@Nonnull BeanMetadata source, String name, @Nonnull Type type,
            @Nonnull AnnotationMetadata annotations) {
        DependencyResolvingResult result = this.resolver.resolve(source, name, type, annotations);
        if (this.ignoreDynamicRouting(source, type)) {
            return result;
        }
        return new DynamicRoutingProxyDecorator(source, name, type, annotations, result);
    }

    private boolean hasMultipleInstances(Type type) {
        Class<?> clazz = TypeUtils.toClass(type);
        return clazz == List.class || clazz == Map.class;
    }

    private boolean ignoreDynamicRouting(BeanMetadata source, Type type) {
        if (this.hasMultipleInstances(type)) {
            // 如果依赖的类型是多实例的聚合类型，则不需要注入路由代理，直接返回解析结果。
            return true;
        }
        Class<?> clazz = TypeUtils.toClass(type);
        if (!clazz.isInterface()) {
            return true;
        }
        return !this.hasGenericableMethod(source, clazz);
    }

    private boolean hasGenericableMethod(BeanMetadata source, Class<?> clazz) {
        AnnotationMetadataResolver metadataResolver = source.runtime().resolverOfAnnotations();
        for (Method method : clazz.getMethods()) {
            AnnotationMetadata annotations = metadataResolver.resolve(method);
            if (annotations.isAnnotationPresent(Genericable.class)) {
                return true;
            }
        }
        return false;
    }

    private static class DynamicRoutingProxyDecorator implements DependencyResolvingResult {
        private final String name;
        private final Class<?> clazz;
        private final AnnotationMetadata annotations;
        private final DependencyResolvingResult result;
        private final LazyLoader<BrokerClient> brokerClientLazyLoader;
        private final List<AopProxyFactory> factories = AopProxyFactory.all();

        private DynamicRoutingProxyDecorator(BeanMetadata source, String name, Type type,
                AnnotationMetadata annotations, DependencyResolvingResult result) {
            this.name = name;
            this.clazz = TypeUtils.toClass(type);
            this.annotations = annotations;
            this.result = result;
            this.brokerClientLazyLoader = new LazyLoader<>(() -> source.container()
                    .lookup(BrokerClient.class)
                    .orElseThrow(() -> new IllegalStateException("No broker client."))
                    .get());
        }

        @Override
        public boolean resolved() {
            for (AopProxyFactory factory : this.factories) {
                if (factory.support(this.clazz)) {
                    return true;
                }
            }
            return this.result.resolved();
        }

        @Override
        public Object get() {
            return this.getProxy().orElseGet(this.result::get);
        }

        private Optional<Object> getProxy() {
            InterceptSupport support = this.constructInterceptSupport();
            for (AopProxyFactory factory : this.factories) {
                if (factory.support(this.clazz)) {
                    return Optional.of(factory.createProxy(support));
                }
            }
            return Optional.empty();
        }

        private InterceptSupport constructInterceptSupport() {
            MethodInterceptor methodInterceptor = this.constructMethodInterceptor();
            return new DefaultInterceptSupport(this.clazz,
                    this.result.resolved() ? this.result::get : () -> null,
                    Collections.singletonList(methodInterceptor));
        }

        private MethodInterceptor constructMethodInterceptor() {
            Fit annotation = this.annotations.getAnnotation(Fit.class);
            int retry = Optional.ofNullable(annotation).map(Fit::retry).orElse(0);
            int timeout = Optional.ofNullable(annotation).map(Fit::timeout).orElse(3000);
            TimeUnit timeoutUnit = Optional.ofNullable(annotation).map(Fit::timeunit).orElse(TimeUnit.MILLISECONDS);
            CommunicationProtocol protocol =
                    Optional.ofNullable(annotation).map(Fit::protocol).orElse(CommunicationProtocol.UNKNOWN);
            SerializationFormat format =
                    Optional.ofNullable(annotation).map(Fit::format).orElse(SerializationFormat.UNKNOWN);
            MethodInterceptor interceptor = new DynamicRoutingInterceptor(this.brokerClientLazyLoader,
                    this.name,
                    retry,
                    timeout,
                    timeoutUnit,
                    protocol,
                    format);
            interceptor.getPointCut().matchers().add(MethodMatcher.accessible());
            interceptor.getPointCut().add(this.clazz);
            return interceptor;
        }
    }
}
