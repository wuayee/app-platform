/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fitframework.runtime.shared;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.plugin.SharedJarRegistry;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.ReflectionUtils;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 为 {@link SharedJarRegistry} 提供基于 {@link ClassLoader} 的实现。
 *
 * @author 梁济时 l00815032
 * @since 2022-06-27
 */
public class ClassLoaderSharedJarRegistry implements SharedJarRegistry {
    private final URLClassLoader loader;
    private final Method method;

    /**
     * 使用类加载程序创建 {@link ClassLoaderSharedJarRegistry} 类的新实例。
     *
     * @param loader 表示将JAR注册到的类加载程序的 {@link ClassLoader}。
     * @throws IllegalArgumentException {@code loader} 为 {@code null} 或不是 {@link URLClassLoader}。
     */
    public ClassLoaderSharedJarRegistry(ClassLoader loader) {
        if (notNull(loader, "The class loader to register JARs cannot be null.") instanceof URLClassLoader) {
            this.loader = ObjectUtils.cast(loader);
            this.method = ReflectionUtils.getDeclaredMethod(URLClassLoader.class, "addURL", URL.class);
            this.method.setAccessible(true);
        } else {
            throw new IllegalArgumentException(StringUtils.format(
                    "The class loader to register JARs is not a URL class loader. [loader={0}]",
                    loader.getClass().getName()));
        }
    }

    @Override
    public void register(URL jar) {
        ReflectionUtils.invoke(this.loader, this.method, jar);
    }
}
