/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package modelengine.fitframework.aop.interceptor.cache.instance;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.cache.Cache;
import modelengine.fitframework.util.ObjectUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 表示 {@link Cache} 的 {@link ConcurrentHashMap} 的实现。
 *
 * @author 季聿阶
 * @since 2022-12-13
 */
public class ConcurrentMapCache extends AbstractCache {
    private final ConcurrentMap<Object, Object> cache;

    /**
     * 创建 {@link ConcurrentMap} 实现的缓存实例。
     *
     * @param name 表示缓存名的 {@link String}。
     */
    public ConcurrentMapCache(String name) {
        this(name, false);
    }

    /**
     * 创建 {@link ConcurrentMap} 实现的缓存实例。
     *
     * @param name 表示缓存名的 {@link String}。
     * @param allowsNullValue 表示是否允许缓存值为 null 的标识的 {@link boolean}。
     */
    public ConcurrentMapCache(String name, boolean allowsNullValue) {
        this(name, allowsNullValue, null);
    }

    /**
     * 创建 {@link ConcurrentMap} 实现的缓存实例。
     *
     * @param name 表示缓存名的 {@link String}。
     * @param allowsNullValue 表示是否允许缓存值为 null 的标识的 {@link boolean}。
     * @param cache 表示默认需要存入的缓存实例的 {@link ConcurrentHashMap}{@code <}{@link Object}{@code , }{@link Object}
     * {@code >}。
     */
    public ConcurrentMapCache(String name, boolean allowsNullValue, ConcurrentMap<Object, Object> cache) {
        super(name, allowsNullValue);
        this.cache = ObjectUtils.getIfNull(cache, ConcurrentHashMap::new);
    }

    @Override
    public boolean contains(Object key) {
        notNull(key, "The cache key cannot be null.");
        return this.cache.containsKey(key);
    }

    @Override
    protected void store(Object key, Object value) {
        this.cache.put(key, value);
    }

    @Override
    public Object load(Object key) {
        return this.cache.get(key);
    }

    @Override
    public void remove(Object key) {
        notNull(key, "The cache key cannot be null.");
        this.cache.remove(key);
    }
}
