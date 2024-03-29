/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.merge.support;

import com.huawei.fitframework.merge.Conflict;
import com.huawei.fitframework.merge.ConflictResolver;

/**
 * 表示取消执行的冲突处理器。
 * <p>取消执行策略指的是直接抛出冲突异常。</p>
 *
 * @param <K> 表示冲突键的类型的 {@link K}。
 * @param <V> 表示冲突值的类型的 {@link V}。
 * @author 季聿阶 j00559309
 * @since 2022-07-30
 */
public class AbortConflictResolver<K, V> implements ConflictResolver<K, V, Conflict<K>> {
    @Override
    public Result<V> resolve(V v1, V v2, Conflict<K> context) {
        return Result.<V>builder().resolved(false).build();
    }
}
