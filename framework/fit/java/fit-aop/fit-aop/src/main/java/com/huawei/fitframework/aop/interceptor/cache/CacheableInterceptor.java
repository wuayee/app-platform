/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.cache;

import com.huawei.fitframework.aop.interceptor.MethodInvocation;
import com.huawei.fitframework.aop.interceptor.MethodJoinPoint;
import com.huawei.fitframework.cache.Cache;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.inspection.Nullable;
import com.huawei.fitframework.ioc.BeanContainer;

import java.util.List;

/**
 * 表示 {@link com.huawei.fitframework.cache.annotation.Cacheable} 的方法拦截器。
 *
 * @author 季聿阶 j00559309
 * @since 2022-12-12
 */
public class CacheableInterceptor extends AbstractCacheInterceptor {
    public CacheableInterceptor(BeanContainer container, KeyGenerator keyGenerator, List<String> cacheNames) {
        super(container, keyGenerator, cacheNames);
    }

    @Nullable
    @Override
    public Object intercept(@Nonnull MethodJoinPoint methodJoinPoint) throws Throwable {
        MethodInvocation invocation = methodJoinPoint.getProxiedInvocation();
        CacheKey key = this.getKeyGenerator()
                .generate(invocation.getTarget(), invocation.getMethod(), invocation.getArguments());
        for (Cache instance : this.getCacheInstances()) {
            if (instance.contains(key)) {
                return instance.get(key);
            }
        }
        Object result = methodJoinPoint.proceed();
        this.getCacheInstances().forEach(instance -> instance.put(key, result));
        return result;
    }
}
