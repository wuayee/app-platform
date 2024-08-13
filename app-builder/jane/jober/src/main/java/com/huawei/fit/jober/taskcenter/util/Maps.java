/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 为 {@link Map} 提供工具类。
 *
 * @author 梁济时
 * @since 2023-12-11
 */
public class Maps {
    private Maps() {
    }

    /**
     * 为 {@link java.util.stream.Collectors#toMap(Function, Function, BinaryOperator, Supplier)} 提供值的合并方法。
     *
     * @param <U> 表示值的类型。
     * @return 表示值的合并方法的 {@link BinaryOperator}。
     */
    public static <U> BinaryOperator<U> throwingMerger() {
        return (key, value) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", key));
        };
    }

    /**
     * 检查两个映射是否包含相同的数据。
     *
     * @param m1 表示待检查的第一个映射的 {@link Map}。
     * @param m2 表示待检查的第二个映射的 {@link Map}。
     * @return 若包含相同的数据，则为 {@code true}，否则为 {@code false}。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean equals(Map m1, Map m2) {
        if (m1 == null) {
            return m2 == null;
        }
        if (m2 == null || m1.size() != m2.size()) {
            return false;
        }
        Set differentKeys = new HashSet(m1.keySet());
        differentKeys.removeAll(m2.keySet());
        if (!differentKeys.isEmpty()) {
            return false;
        }
        Set<Map.Entry> entries = m1.entrySet();
        for (Map.Entry entry : entries) {
            Object value1 = entry.getValue();
            Object value2 = m2.get(entry.getKey());
            if (!Objects.equals(value1, value2)) {
                return false;
            }
        }
        return true;
    }
}
