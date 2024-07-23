/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.test.domain.listener;

import com.huawei.fitframework.test.annotation.EnableMybatis;
import com.huawei.fitframework.test.domain.resolver.TestContextConfiguration;
import com.huawei.fitframework.test.domain.util.AnnotationUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * 用于字段注入 mybatis 的监听器。
 *
 * @author 易文渊
 * @since 2024-07-21
 */
public class MybatisTestListener implements TestListener {
    private static final Set<String> DEFAULT_SCAN_PACKAGES =
            new HashSet<>(Arrays.asList("com.huawei.fitframework.transaction", "com.huawei.fit.integration.mybatis"));

    @Override
    public Optional<TestContextConfiguration> config(Class<?> clazz) {
        if (!AnnotationUtils.getAnnotation(clazz, EnableMybatis.class).isPresent()) {
            return Optional.empty();
        }
        TestContextConfiguration configuration =
                TestContextConfiguration.custom().testClass(clazz).scannedPackages(DEFAULT_SCAN_PACKAGES).build();
        return Optional.of(configuration);
    }
}