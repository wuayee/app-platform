/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fitframework.merge;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.merge.support.DefaultConflictResolverCollection;

/**
 * 表示冲突处理器的集合。
 *
 * @author 季聿阶
 * @since 2022-08-02
 */
public interface ConflictResolverCollection {
    /**
     * 向冲突处理器集合中添加默认的冲突处理器。
     *
     * @param resolver 表示默认的冲突处理器的 {@link ConflictResolver}{@code <}{@link Object}{@code
     * , }{@link Object}{@code , }{@link Conflict}{@code <}{@link Object}{@code >>}。
     * @return 表示当前冲突处理器集合的 {@link ConflictResolverCollection}。
     */
    @Nonnull
    ConflictResolverCollection add(ConflictResolver<Object, Object, Conflict<Object>> resolver);

    /**
     * 向冲突处理器集合中添加一个指定类型的冲突处理器。
     *
     * @param clazz 表示指定类型的 {@link Class}{@code <}{@link V}{@code >}。
     * @param resolver 表示待添加的冲突处理器的 {@link ConflictResolver}{@code <}{@link K}{@code , }{@link
     * V}{@code , }{@link C}{@code >}。
     * @param <K> 表示冲突键的类型的 {@link K}。
     * @param <V> 表示待处理冲突的值的类型的 {@link V}。
     * @param <C> 表示冲突上下文类型的 {@link C}。
     * @return 表示当前冲突处理器集合的 {@link ConflictResolverCollection}。
     * @throws IllegalArgumentException 当 {@code clazz} 为 {@code null} 时。
     */
    @Nonnull
    <K, V, C extends Conflict<K>> ConflictResolverCollection add(Class<V> clazz, ConflictResolver<K, V, C> resolver);

    /**
     * 获取指定类型的冲突处理器。
     *
     * @param clazz 表示指定类型的 {@link Class}{@code <}{@link V}{@code >}。
     * @param <K> 表示冲突键的类型的 {@link K}。
     * @param <V> 表示待处理冲突的值的类型的 {@link V}。
     * @param <C> 表示冲突上下文类型的 {@link C}。
     * @return 表示指定类型的冲突处理器的 {@link ConflictResolver}{@code <}{@link K}{@code , }{@link V}{@code ,
     * }{@link C}{@code >}。
     * @throws IllegalArgumentException 当 {@code clazz} 为 {@code null} 时。
     */
    @Nonnull
    <K, V, C extends Conflict<K>> ConflictResolver<K, V, C> get(Class<V> clazz);

    /**
     * 创建一个空的冲突处理器的集合。
     *
     * @return 表示创建的空的冲突处理器的集合的 {@link ConflictResolverCollection}。
     */
    static ConflictResolverCollection create() {
        return new DefaultConflictResolverCollection();
    }
}
