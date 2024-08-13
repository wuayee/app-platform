/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.beans;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.util.CollectionUtils;

import java.util.Set;

/**
 * Bean 的工具类。
 *
 * @author 季聿阶
 * @since 2023-02-07
 */
public class BeanUtils {
    /**
     * 将来源对象的属性拷贝到目标对象中去。
     *
     * @param source 表示来源对象的 {@link Object}。
     * @param target 表示目标对象的 {@link Object}。
     * @throws IllegalArgumentException 当 {@code source} 或 {@code target} 为 {@code null} 时。
     */
    public static void copyProperties(Object source, Object target) {
        notNull(source, "The source object cannot be null.");
        notNull(target, "The target object cannot be null.");
        BeanAccessor srcAccessor = BeanAccessor.of(source.getClass());
        BeanAccessor dstAccessor = BeanAccessor.of(target.getClass());
        Set<String> properties = CollectionUtils.intersect(srcAccessor.properties(), dstAccessor.properties());
        properties.remove("class");
        for (String property : properties) {
            Object value = srcAccessor.get(source, property);
            dstAccessor.set(target, property, value);
        }
    }
}
