/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.merge.support;

import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.merge.Conflict;
import com.huawei.fitframework.merge.ConflictResolver;
import com.huawei.fitframework.merge.ConflictResolverCollection;
import com.huawei.fitframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link ConflictResolverCollection} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2022-07-31
 */
public class DefaultConflictResolverCollection implements ConflictResolverCollection {
    private final Map<Class<?>, ConflictResolver<?, ?, ?>> resolvers = new HashMap<>();
    private ConflictResolver<Object, Object, Conflict<Object>> defaultResolver = new AbortConflictResolver<>();

    @Nonnull
    @Override
    public ConflictResolverCollection add(ConflictResolver<Object, Object, Conflict<Object>> resolver) {
        if (resolver != null) {
            this.defaultResolver = resolver;
        }
        return this;
    }

    @Nonnull
    @Override
    public <K, V, C extends Conflict<K>> ConflictResolverCollection add(Class<V> clazz,
            ConflictResolver<K, V, C> resolver) {
        Validation.notNull(clazz, "The class to register conflict resolver cannot be null.");
        if (resolver == null) {
            this.resolvers.remove(clazz);
        } else {
            this.resolvers.put(clazz, resolver);
        }
        return this;
    }

    @Nonnull
    @Override
    public <K, V, C extends Conflict<K>> ConflictResolver<K, V, C> get(Class<V> clazz) {
        Validation.notNull(clazz, "The class to get conflict resolver cannot be null.");
        ConflictResolver<K, V, C> actual = null;
        for (Map.Entry<Class<?>, ConflictResolver<?, ?, ?>> entry : this.resolvers.entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) {
                actual = ObjectUtils.cast(entry.getValue());
                break;
            }
        }
        if (actual != null) {
            return actual;
        }
        return ObjectUtils.cast(this.defaultResolver);
    }
}
