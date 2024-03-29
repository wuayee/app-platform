/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.cache.support;

import com.huawei.fitframework.aop.interceptor.MethodInterceptor;
import com.huawei.fitframework.aop.interceptor.cache.EvictCacheInterceptor;
import com.huawei.fitframework.aop.interceptor.cache.KeyGenerator;
import com.huawei.fitframework.cache.annotation.EvictCache;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示 {@link EvictCache} 注解的方法拦截器工厂。
 *
 * @author 季聿阶 j00559309
 * @since 2022-12-14
 */
public class EvictCacheInterceptorFactory extends AbstractCacheInterceptorFactory<EvictCache> {
    public EvictCacheInterceptorFactory(BeanContainer container) {
        super(container, EvictCache.class);
    }

    @Override
    protected List<String> cacheInstanceNames(@Nonnull EvictCache annotation) {
        return Stream.of(annotation.name()).filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    @Override
    protected String cacheKeyPattern(@Nonnull EvictCache annotation) {
        return annotation.key();
    }

    @Override
    protected MethodInterceptor create(BeanContainer container, KeyGenerator keyGenerator, List<String> cacheNames) {
        return new EvictCacheInterceptor(container, keyGenerator, cacheNames);
    }
}
