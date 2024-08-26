/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package modelengine.fitframework.aop;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.aop.interceptor.MethodInterceptor;
import modelengine.fitframework.aop.interceptor.MethodInterceptorResolver;
import modelengine.fitframework.aop.interceptor.async.AsyncInterceptorResolver;
import modelengine.fitframework.aop.interceptor.cache.CacheInterceptorResolver;
import modelengine.fitframework.aop.interceptor.support.MethodInterceptorComparator;
import modelengine.fitframework.aop.proxy.AopProxyFactory;
import modelengine.fitframework.aop.proxy.InterceptSupport;
import modelengine.fitframework.aop.proxy.support.DefaultInterceptSupport;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.lifecycle.bean.BeanLifecycle;
import modelengine.fitframework.ioc.lifecycle.bean.BeanLifecycleInterceptor;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.TypeUtils;

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
