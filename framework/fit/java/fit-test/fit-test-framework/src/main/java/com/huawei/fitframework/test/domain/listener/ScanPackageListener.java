/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.test.domain.listener;

import com.huawei.fitframework.annotation.ScanPackages;
import com.huawei.fitframework.test.domain.resolver.TestContextConfiguration;
import com.huawei.fitframework.test.domain.util.AnnotationUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

/**
 * 表示包扫描器的监听器。
 *
 * @author 易文渊
 * @since 2024-07-26
 */
public class ScanPackageListener implements TestListener {
    @Override
    public Optional<TestContextConfiguration> config(Class<?> clazz) {
        return AnnotationUtils.getAnnotation(clazz, ScanPackages.class)
                .map(annotation -> TestContextConfiguration.custom()
                        .testClass(clazz)
                        .scannedPackages(new HashSet<>(Arrays.asList(annotation.value())))
                        .build());
    }
}