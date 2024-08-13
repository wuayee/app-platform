/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.aop;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.aop.interceptor.MethodInterceptor;
import com.huawei.fitframework.aop.interceptor.MethodInterceptorResolver;
import com.huawei.fitframework.aop.interceptor.async.AsyncInterceptorResolver;
import com.huawei.fitframework.aop.interceptor.cache.CacheInterceptorResolver;
import com.huawei.fitframework.aop.interceptor.support.MethodInterceptorComparator;
import com.huawei.fitframework.aop.proxy.AopProxyFactory;
import com.huawei.fitframework.aop.proxy.InterceptSupport;
import com.huawei.fitframework.aop.proxy.support.DefaultInterceptSupport;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanMetadata;
import com.huawei.fitframework.ioc.lifecycle.bean.BeanLifecycle;
import com.huawei.fitframework.ioc.lifecycle.bean.BeanLifecycleInterceptor;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.LazyLoader;
import com.huawei.fitframework.util.TypeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * {@link BeanLifecycleInterceptor} 的 AOP 实现。
 *
 * @author 季聿阶
 * @since 2022-05-01
 */
public class AopInterceptor implements BeanLifecycleInterceptor {
    private final BeanContainer container;
    private final LazyLoader<List<MethodInterceptorResolver>> methodInterceptorResolversLoader;
    private final List<AopProxyFactory> aopProxyFactories = AopProxyFactory.all();
    private final MethodInterceptorComparator methodInterceptorComparator = new MethodInterceptorComparator();

    public AopInterceptor(BeanContainer container) {
        this.container = notNull(container, "The bean container cannot be null.");
        this.methodInterceptorResolversLoader = new LazyLoader<>(this::getMethodInterceptorResolvers);
    }

    @Override
    public boolean isInterceptionRequired(BeanMetadata metadata) {
        // 只有所有的方法拦截器的解析器都能解析，才能进行 AOP
        return this.methodInterceptorResolversLoader.get().stream().noneMatch(resolver -> resolver.eliminate(metadata));
    }

    @Override
    public Object decorate(BeanLifecycle lifecycle, Object bean) {
        Object initializedBean = lifecycle.decorate(bean);
        if (CollectionUtils.isEmpty(this.methodInterceptorResolversLoader.get())) {
            return initializedBean;
        }
        List<MethodInterceptor> methodInterceptors = this.methodInterceptorResolversLoader.get()
                .stream()
                .map(resolver -> resolver.resolve(lifecycle.metadata(), bean))
                .flatMap(List::stream)
                .sorted(this.methodInterceptorComparator)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(methodInterceptors)) {
            return initializedBean;
        }
        InterceptSupport support = new DefaultInterceptSupport(TypeUtils.toClass(lifecycle.metadata().type()),
                () -> initializedBean,
                methodInterceptors);
        return this.createAopProxy(support);
    }

    private List<MethodInterceptorResolver> getMethodInterceptorResolvers() {
        List<MethodInterceptorResolver> resolvers = new ArrayList<>();
        resolvers.add(new AsyncInterceptorResolver());
        resolvers.add(new CacheInterceptorResolver(this.container));
        ServiceLoader<MethodInterceptorResolver> loader =
                ServiceLoader.load(MethodInterceptorResolver.class, this.getClass().getClassLoader());
        loader.forEach(resolvers::add);
        return resolvers;
    }

    private Object createAopProxy(InterceptSupport support) {
        for (AopProxyFactory proxyFactory : this.aopProxyFactories) {
            if (proxyFactory.support(support.getTargetClass())) {
                return proxyFactory.createProxy(support);
            }
        }
        return support.getTarget();
    }
}
