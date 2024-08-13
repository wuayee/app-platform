/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.runtime.aggregated;

import com.huawei.fitframework.plugin.RootPlugin;
import com.huawei.fitframework.protocol.jar.JarLocation;
import com.huawei.fitframework.runtime.FitRuntime;
import com.huawei.fitframework.runtime.support.AbstractFitRuntime;
import com.huawei.fitframework.util.ClassUtils;
import com.huawei.fitframework.util.FileUtils;
import com.huawei.fitframework.util.ObjectUtils;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 为 FIT 运行时提供聚合启动场景的实现。
 * <p>聚合启动指的是所有的 Jar 包合并为一个 Jar in Jar 的单独 Jar 包进行启动。</p>
 *
 * @author 梁济时
 * @since 2023-01-31
 */
public final class AggregatedFitRuntime extends AbstractFitRuntime {
    /**
     * 使用入口类和命令行参数来初始化 {@link AggregatedFitRuntime} 类的新实例。
     *
     * @param entry 表示入口类的 {@link Class}{@code <?>}。
     * @param args 表示命令行参数的 {@link String}{@code []}。
     */
    public AggregatedFitRuntime(Class<?> entry, String[] args) {
        super(entry, args);
    }

    @Override
    protected URL locateRuntime() {
        URL domain = ClassUtils.locateOfProtectionDomain(AggregatedFitRuntime.class);
        JarLocation location = JarLocation.parse(domain);
        return FileUtils.urlOf(location.file());
    }

    @Override
    protected URLClassLoader obtainSharedClassLoader() {
        // FitRuntime 在 fit-api 包中，因此该类的类加载器可以作为共享的类加载器
        return ObjectUtils.cast(FitRuntime.class.getClassLoader());
    }

    @Override
    protected RootPlugin createRootPlugin() {
        return new AggregatedRootPlugin(this);
    }
}
