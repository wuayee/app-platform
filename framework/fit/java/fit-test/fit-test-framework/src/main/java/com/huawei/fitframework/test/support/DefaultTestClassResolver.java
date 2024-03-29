/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.test.support;

import com.huawei.fitframework.annotation.ScanPackages;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.ioc.lifecycle.container.BeanContainerInitializedObserver;
import com.huawei.fitframework.runtime.FitRuntime;
import com.huawei.fitframework.test.TestClassResolver;
import com.huawei.fitframework.test.TestContextConfiguration;
import com.huawei.fitframework.test.annotation.FitTestWithJunit;
import com.huawei.fitframework.test.annotation.Mocked;
import com.huawei.fitframework.test.mock.MockBean;
import com.huawei.fitframework.test.plugin.TestPlugin;
import com.huawei.fitframework.test.runtime.TestFitRuntime;
import com.huawei.fitframework.test.util.AnnotationUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.ReflectionUtils;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 默认的单测类解析器。
 *
 * @author 邬涨财 w00575064
 * @since 2023-01-17
 */
public class DefaultTestClassResolver implements TestClassResolver {
    private final Set<String> defaultScanPackages =
            new HashSet<>(Arrays.asList("com.huawei.fitframework.test", "com.huawei.fit.integration.mockito"));

    @Override
    public TestPlugin resolve(Class<?> clazz) {
        TestContextConfiguration configuration = this.buildTestContextConfiguration(clazz);
        FitRuntime runtime = new TestFitRuntime(clazz);
        runtime.start();
        Map<TestContextConfiguration, TestPlugin> allPlugins = TestPlugin.allPlugins();
        if (allPlugins.containsKey(configuration)) {
            return allPlugins.get(configuration);
        }
        TestPlugin plugin = ObjectUtils.cast(runtime.root());
        plugin.initialize();
        TestPlugin.put(configuration, plugin);
        this.registryBeans(configuration, plugin);
        this.registryMockBeans(clazz, plugin.container());
        BeanContainerInitializedObserver.notify(plugin.container());
        return plugin;
    }

    private TestContextConfiguration buildTestContextConfiguration(Class<?> clazz) {
        return TestContextConfiguration.custom()
                .testClass(clazz)
                .classes(this.resolveClass(this.getTestConfigurationClass(clazz)))
                .build();
    }

    private Class<?> getTestConfigurationClass(Class<?> clazz) {
        Class<?> superclass = clazz;
        while (superclass != null) {
            if (this.isTestConfigurationClass(superclass)) {
                return superclass;
            }
            superclass = superclass.getSuperclass();
        }
        return clazz;
    }

    private boolean isTestConfigurationClass(Class<?> clazz) {
        return AnnotationUtils.getAnnotation(clazz, RunWith.class).isPresent() || AnnotationUtils.getAnnotation(clazz,
                ExtendWith.class).isPresent();
    }

    private void registryBeans(TestContextConfiguration configuration, TestPlugin plugin) {
        Set<String> basePackages = Arrays.stream(configuration.classes())
                .flatMap(resolvedClass -> this.getBasePackages(resolvedClass).stream())
                .collect(Collectors.toSet());
        basePackages.addAll(this.defaultScanPackages);
        plugin.scan(basePackages);
        BeanContainer container = plugin.container();
        Arrays.stream(configuration.classes())
                .filter(resolvedClass -> !container.lookup(resolvedClass).isPresent())
                .forEach(resolvedClass -> container.registry().register(resolvedClass));
    }

    private void registryMockBeans(Class<?> clazz, BeanContainer container) {
        Field[] fields = ReflectionUtils.getDeclaredFields(clazz);
        Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(Mocked.class))
                .forEach(field -> this.registerMockedBean(container, field));
    }

    private void registerMockedBean(BeanContainer container, Field field) {
        container.all(field.getType()).forEach(beanFactory -> container.removeBean(beanFactory.metadata().name()));
        Object bean = container.lookup(MockBean.class)
                .map(BeanFactory::<MockBean>get)
                .orElseThrow(() -> new IllegalStateException(
                        "Failed to register mock bean: cannot find implements of AbstractMockBean."))
                .getBean(field);
        container.registry().register(bean);
    }

    private Set<String> getBasePackages(Class<?> clazz) {
        Optional<ScanPackages> opScanPackagesAnnotation = AnnotationUtils.getAnnotation(clazz, ScanPackages.class);
        if (!opScanPackagesAnnotation.isPresent()) {
            return new HashSet<>();
        }
        Set<String> basePackages = new HashSet<>(Arrays.asList(opScanPackagesAnnotation.get().value()));
        if (basePackages.isEmpty()) {
            basePackages.add(clazz.getPackage().getName());
        }
        return basePackages;
    }

    private Class<?>[] resolveClass(AnnotatedElement element) {
        Optional<FitTestWithJunit> opAnnotation = AnnotationUtils.getAnnotation(element, FitTestWithJunit.class);
        if (!opAnnotation.isPresent()) {
            return new Class<?>[0];
        }
        FitTestWithJunit fitTest = opAnnotation.get();
        return fitTest.classes();
    }
}
