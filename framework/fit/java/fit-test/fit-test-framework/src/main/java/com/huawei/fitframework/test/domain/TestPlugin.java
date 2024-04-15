/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.test.domain;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.ioc.BeanFactory;
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
import com.huawei.fitframework.test.domain.resolver.MockBean;
import com.huawei.fitframework.test.domain.resolver.TestContextConfiguration;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    private final FitRuntime runtime;
    private final ClassLoader loader;
    private final PluginMetadata metadata;
    private final PackageScanner packageScanner;
    private final TestContextConfiguration configuration;

    /**
     * 通过运行时对象和 Bean 配置来初始化 {@link TestPlugin} 的新实例。
     *
     * @param runtime 表示运行时对象的 {@link FitRuntime}。
     * @param configuration 表示待注册 Bean 相关的配置的 {@link TestContextConfiguration}。
     */
    public TestPlugin(FitRuntime runtime, TestContextConfiguration configuration) {
        this.runtime = Validation.notNull(runtime, "The runtime to create test plugin cannot be null.");
        this.loader = TestPlugin.class.getClassLoader();
        PluginKey pluginKey = new DefaultPluginKey(UNKNOWN_GROUP, PLUGIN_NAME, Version.parse(UNKNOWN_VERSION));
        this.metadata =
                new DefaultPluginMetadata(pluginKey, this.runtime.location(), PluginCategory.SYSTEM, Integer.MIN_VALUE);
        this.configuration =
                Validation.notNull(configuration, "The configuration to create test plugin cannot be null.");
        this.packageScanner = this.scanner((packageScanner, clazz) -> this.onClassDetected(packageScanner,
                clazz,
                Arrays.stream(this.configuration.classes()).collect(Collectors.toSet())));
    }

    @Override
    public ClassLoader pluginClassLoader() {
        return this.loader;
    }

    @Override
    public FitRuntime runtime() {
        return this.runtime;
    }

    @Override
    public PluginMetadata metadata() {
        return this.metadata;
    }

    @Override
    protected void scanBeans() {
        this.registerBean(configuration.classes());
        this.scan(configuration.scannedPackages());
        this.registerMockedBean(configuration.mockedBeanFields());
    }

    @Override
    protected void loadPlugins() {}

    private void onClassDetected(PackageScanner scanner, Class<?> clazz, Set<Class<?>> classes) {
        if (classes.contains(clazz)) {
            return;
        }
        List<BeanMetadata> beans = this.container().registry().register(clazz);
        for (BeanMetadata bean : beans) {
            Set<String> basePackages = this.runtime().resolverOfBeans().packages(bean);
            scanner.scan(basePackages);
        }
    }

    private void registerBean(Class<?>[] classArray) {
        Arrays.stream(classArray)
                .filter(clazz -> !this.container().lookup(clazz).isPresent())
                .forEach(clazz -> this.container().registry().register(clazz));
    }

    private void scan(Set<String> basePackages) {
        this.packageScanner.scan(basePackages);
    }

    private void registerMockedBean(Set<Field> mockedBeanFields) {
        for (Field field : mockedBeanFields) {
            Object bean = this.container()
                    .lookup(MockBean.class)
                    .map(BeanFactory::<MockBean>get)
                    .orElseThrow(() -> new IllegalStateException(
                            "Failed to register mock bean: cannot find implements of AbstractMockBean."))
                    .getBean(field);
            this.container().registry().register(bean);
        }
    }
}
