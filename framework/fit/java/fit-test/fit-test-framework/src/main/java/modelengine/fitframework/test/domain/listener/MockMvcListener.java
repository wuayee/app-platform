/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fitframework.test.domain.listener;

import modelengine.fitframework.test.annotation.EnableMockMvc;
import modelengine.fitframework.test.domain.TestContext;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.resolver.TestContextConfiguration;
import modelengine.fitframework.test.domain.util.AnnotationUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * 用于注入 mockMvc 的监听器。
 *
 * @author 易文渊
 * @since 2024-07-21
 */
public class MockMvcListener implements TestListener {
    private static final Set<String> DEFAULT_SCAN_PACKAGES =
            new HashSet<>(Arrays.asList("com.huawei.fit.server", "com.huawei.fit.http"));

    private final int port;

    /**
     * 通过插件端口初始化 {@link MockMvcListener} 的实例。
     *
     * @param port 表示插件启动端口的 {code int}。
     */
    public MockMvcListener(int port) {
        this.port = port;
    }

    @Override
    public Optional<TestContextConfiguration> config(Class<?> clazz) {
        if (!AnnotationUtils.getAnnotation(clazz, EnableMockMvc.class).isPresent()) {
            return Optional.empty();
        }
        TestContextConfiguration configuration =
                TestContextConfiguration.custom().testClass(clazz).scannedPackages(DEFAULT_SCAN_PACKAGES).build();
        return Optional.of(configuration);
    }

    @Override
    public void beforeTestClass(TestContext context) {
        Class<?> testClass = context.testClass();
        if (!AnnotationUtils.getAnnotation(testClass, EnableMockMvc.class).isPresent()) {
            return;
        }
        MockMvc mockMvc = new MockMvc(this.port);
        context.plugin().container().registry().register(mockMvc);
    }
}