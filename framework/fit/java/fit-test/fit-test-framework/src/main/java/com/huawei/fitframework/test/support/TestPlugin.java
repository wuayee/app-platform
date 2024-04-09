/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.test.support;

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
import com.huawei.fitframework.test.mock.MockBean;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
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

    private final FitRuntime runtime;
    private final ClassLoader loader;
    private final PluginMetadata metadata;

    private TestContextConfiguration configuration;

    /**
     * 通过运行时对象和 bean 配置来初始化 {@link TestPlugin}。
     *
     * @param runtime 运行时对象 {@link FitRuntime}。
     * @param configuration 待注册bean相关的配置 {@link TestContextConfiguration}。
     */
    public TestPlugin(FitRuntime runtime, TestContextConfiguration configuration) {
        this.runtime = Validation.notNull(runtime, "The runtime to create test plugin cannot be null.");
        this.loader = TestPlugin.class.getClassLoader();
        PluginKey pluginKey = new DefaultPluginKey(UNKNOWN_GROUP, PLUGIN_NAME, Version.parse(UNKNOWN_VERSION));
        this.metadata =
                new DefaultPluginMetadata(pluginKey, this.runtime.location(), PluginCategory.SYSTEM, Integer.MIN_VALUE);
        this.configuration =
                Validation.notNull(configuration, "The configuration to create test plugin cannot be null.");
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

    /**
     * 依据配置注册 Bean。
     *
     * @param configuration 注册 Bean 依赖的配置 {@link TestContextConfiguration}。
     */
    public void registerBean(TestContextConfiguration configuration) {
        this.scan(configuration.scannedPackages());
        this.registerBean(configuration.classes());
        this.registerMockedBean(configuration.mockedBeanFields());
    }

    private void onClassDetected(PackageScanner scanner, Class<?> clazz) {
        List<BeanMetadata> beans = this.container().registry().register(clazz);
        for (BeanMetadata bean : beans) {
            Set<String> basePackages = this.runtime().resolverOfBeans().packages(bean);
            scanner.scan(basePackages);
        }
    }

    @Override
    public PluginMetadata metadata() {
        return this.metadata;
    }

    @Override
    protected void loadPlugins() {
        this.registerBean(this.configuration);
    }

    @Override
    public ClassLoader pluginClassLoader() {
        return this.loader;
    }

    @Override
    public FitRuntime runtime() {
        return this.runtime;
    }

    private void registerBean(Class<?>[] classArray) {
        Arrays.stream(classArray)
                .filter(clazz -> !this.container().lookup(clazz).isPresent())
                .forEach(clazz -> this.container().registry().register(clazz));
    }

    private void registerMockedBean(Set<Field> mockedBeanFields) {
        for (Field field : mockedBeanFields) {
            // 在扫描出的包中，有可能已经包含了和模拟 Bean 相同名称的 Bean，需要删除
            this.container()
                    .all(field.getType())
                    .forEach(beanFactory -> this.container().removeBean(beanFactory.metadata().name()));
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
