/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.aop.util;

import com.huawei.fitframework.inspection.Validation;

import java.util.Objects;
import java.util.Optional;

/**
 * 类加载器相关的工具类。
 *
 * @author 邬涨财
 * @since 2023-03-25
 */
public class ClassLoaderUtils {
    /**
     * 获得两个类加载器公共的子类加载器。
     *
     * @param class1 表示第一个父类加载器的 {@link Class}{@code <?>}。
     * @param class2 表示第二个父类加载器的 {@link Class}{@code <?>}。
     * @return 表示获取到的公共子类加载器的 {@link Optional}{@code <}{@link ClassLoader}{@code >}。
     */
    public static Optional<ClassLoader> getCommonChildClassLoader(Class<?> class1, Class<?> class2) {
        Validation.notNull(class1, "The first class cannot not be null when get child class loader.");
        Validation.notNull(class2, "The second class cannot not be null when get child class loader.");
        ClassLoader classLoader1 = class1.getClassLoader();
        ClassLoader classLoader2 = class2.getClassLoader();
        if (classLoader1 == null) {
            return Optional.of(classLoader2);
        }
        if (classLoader2 == null) {
            return Optional.of(classLoader1);
        }
        ClassLoader tmp = classLoader1;
        while (tmp != null) {
            if (Objects.equals(tmp, classLoader2)) {
                return Optional.of(classLoader1);
            }
            tmp = classLoader1.getParent();
        }
        tmp = classLoader2;
        while (tmp != null) {
            if (Objects.equals(tmp, classLoader1)) {
                return Optional.of(classLoader2);
            }
            tmp = classLoader2.getParent();
        }
        return Optional.empty();
    }
}