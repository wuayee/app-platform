/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.cache.support;

import com.huawei.fitframework.aop.interceptor.cache.CacheKey;
import com.huawei.fitframework.aop.interceptor.cache.KeyGenerator;
import com.huawei.fitframework.inspection.Nonnull;

import java.lang.reflect.Method;

/**
 * 表示 {@link KeyGenerator} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2022-12-13
 */
public class DefaultKeyGenerator implements KeyGenerator {
    /** 表示空的缓存键生成器实现。 */
    public static final KeyGenerator EMPTY = (target, method, params) -> CacheKey.empty();

    @Override
    public CacheKey generate(Object target, @Nonnull Method method, @Nonnull Object... params) {
        return CacheKey.combine(params);
    }
}
