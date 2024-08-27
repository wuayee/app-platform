/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fitframework.util.wildcard;

/**
 * 为通配符提供分类程序。
 *
 * @author 梁济时
 * @since 2022-07-29
 */
@FunctionalInterface
public interface SymbolClassifier<T> {
    /**
     * 对指定条目进行分类。
     *
     * @param item 表示待分类的对象的 {@link Object}。
     * @return 表示条目的通配符类型的 {@link SymbolType}。{@code null} 等同于 {@link SymbolType#NORMAL}。
     */
    SymbolType classify(T item);
}
