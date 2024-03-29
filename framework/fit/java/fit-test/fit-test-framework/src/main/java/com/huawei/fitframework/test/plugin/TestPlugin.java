/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.test.plugin;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.ioc.BeanMetadata;
import com.huawei.fitframework.jvm.scan.PackageScanner;
import com.huawei.fitframework.model.Version;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.plugin.PluginCategory;
import com.huawei.fitframework.plugin.PluginKey;
import com.huawei.fitframework.plugin.PluginMetadata;
import com.huawei.fitframework.plugin.support.DefaultPluginKey;
import com.huawei.fitframework.plugin.support.DefaultPluginMetadata;
import com.huawei.fitframework.runtime.FitRuntime;
import com.huawei.fitframework.runtime.support.AbstractRootPlugin;
import com.huawei.fitframework.test.TestContextConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 测试框架使用的插件类。
 *
 * @author 邬涨财 w00575064
 * @since 2023-01-17
 */
public class TestPlugin extends AbstractRootPlugin implements Plugin {
    private static final String UNKNOWN_GROUP = "test.group";
    private static final String PLUGIN_NAME = "fit-test-framework";
    private static final String UNKNOWN_VERSION = "0.0.0-test";
    private static final Map<TestContextConfiguration, TestPlugin> PLUGINS = new HashMap<>();

    private final FitRuntime runtime;
    private final ClassLoader loader;
    private final PluginMetadata metadata;

    public TestPlugin(FitRuntime runtime) {
        Validation.notNull(runtime, "The run time to create test plugin cannot be null.");
        this.runtime = runtime;
        this.loader = TestPlugin.class.getClassLoader();
        PluginKey pluginKey = new DefaultPluginKey(UNKNOWN_GROUP, PLUGIN_NAME, Version.parse(UNKNOWN_VERSION));
        this.metadata =
                new DefaultPluginMetadata(pluginKey, this.runtime.location(), PluginCategory.SYSTEM, Integer.MIN_VALUE);
    }

    @Override
    protected void scanBeans() {}

    /**
     * 扫描指定包。
     *
     * @param basePackages 表示待扫描的包的 {@link Set}{@code <}{@link String}{@code >}。
     */
    public void scan(Set<String> basePackages) {
        PackageScanner scanner = PackageScanner.forClassLoader(this.pluginClassLoader(), this::onClassDetected);
        Optional.ofNullable(this.runtime().entry()).ifPresent(scanner::scan);
        scanner.scan(basePackages);
    }

    private void onClassDetected(PackageScanner scanner, Class<?> clazz) {
        List<BeanMetadata> beans = this.container().registry().register(clazz);
        for (BeanMetadata bean : beans) {
            Set<String> basePackages = this.runtime().resolverOfBeans().packages(bean);
            scanner.scan(basePackages);
        }
    }

    /**
     * 获取所有插件对象。
     *
     * @return 表示插件对象的 {@link Map}{@code <}{@link TestContextConfiguration}{@code , }{@link TestPlugin}{@code >}。
     */
    public static Map<TestContextConfiguration, TestPlugin> allPlugins() {
        return PLUGINS;
    }

    /**
     * 往插件映射添加插件。
     *
     * @param configuration 表示测试上下文的配置类的 {@link TestContextConfiguration}。
     * @param plugin 表示需要添加的插件的 {@link TestPlugin}。
     */
    public static void put(TestContextConfiguration configuration, TestPlugin plugin) {
        PLUGINS.put(configuration, plugin);
    }

    @Override
    public PluginMetadata metadata() {
        return this.metadata;
    }

    @Override
    protected void loadPlugins() {}

    @Override
    public ClassLoader pluginClassLoader() {
        return this.loader;
    }

    @Override
    public FitRuntime runtime() {
        return this.runtime;
    }
}
