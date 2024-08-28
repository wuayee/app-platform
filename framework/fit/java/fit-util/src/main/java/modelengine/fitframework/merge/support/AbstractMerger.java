/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fitframework.merge.support;

import modelengine.fitframework.merge.ConflictResolverCollection;
import modelengine.fitframework.merge.Merger;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 表示 {@link Merger} 的抽象父类。
 *
 * @param <V> 表示待合并的数据类型的 {@link V}。
 * @author 季聿阶
 * @since 2022-07-31
 */
public abstract class AbstractMerger<V> implements Merger<V> {
    private final ConflictResolverCollection conflictResolvers;

    /**
     * 通过冲突处理器的集合来实例化 {@link AbstractMerger}。
     *
     * @param conflictResolvers 表示冲突处理器的集合的 {@link ConflictResolverCollection}。
     */
    protected AbstractMerger(ConflictResolverCollection conflictResolvers) {
        this.conflictResolvers = ObjectUtils.getIfNull(conflictResolvers, ConflictResolverCollection::create);
    }

    @Override
    public ConflictResolverCollection conflictResolvers() {
        return this.conflictResolvers;
    }
}
