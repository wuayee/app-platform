/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.cache.support;

import com.huawei.fitframework.aop.interceptor.cache.CacheKey;
import com.huawei.fitframework.inspection.Nullable;
import com.huawei.fitframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * 表示 {@link CacheKey} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2022-12-13
 */
public class DefaultCacheKey implements CacheKey {
    /** 表示空的缓存键的单例。 */
    public static final CacheKey EMPTY = new DefaultCacheKey();

    private final Object target;
    private final Method method;
    private final Object[] keys;
    private final int hashcode;

    public DefaultCacheKey(Object... keys) {
        this(null, null, keys);
    }

    public DefaultCacheKey(Object target, Method method, Object... keys) {
        this.target = target;
        this.method = method;
        this.keys = ObjectUtils.getIfNull(keys, () -> new Object[0]);
        this.hashcode = Objects.hash(this.target, this.method, Arrays.deepHashCode(this.keys));
    }

    @Nullable
    @Override
    public Object target() {
        return this.target;
    }

    @Nullable
    @Override
    public Method method() {
        return this.method;
    }

    @Override
    public Object[] params() {
        return this.keys;
    }

    private Object mergeKeys() {
        return this.keys;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (!(another instanceof CacheKey)) {
            return false;
        }
        CacheKey that = ObjectUtils.cast(another);
        return Objects.equals(this.target(), that.target()) && Objects.equals(this.method(), that.target())
                && Arrays.deepEquals(this.params(), that.params());
    }

    @Override
    public int hashCode() {
        return this.hashcode;
    }
}
