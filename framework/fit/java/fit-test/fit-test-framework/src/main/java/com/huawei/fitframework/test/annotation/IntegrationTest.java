/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.test.annotation;

import com.huawei.fitframework.annotation.Forward;
import com.huawei.fitframework.annotation.ScanPackages;
import com.huawei.fitframework.test.adapter.north.junit5.FitExtension;
import com.huawei.fitframework.test.domain.db.DatabaseModel;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于集成测试。
 *
 * @author 易文渊
 * @since 2024-07-26
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ScanPackages
@EnableDataSource
@EnableMybatis
@EnableMockMvc
@ExtendWith(FitExtension.class)
public @interface IntegrationTest {
    /**
     * 指示需要扫描的包。
     *
     * @return 表示待扫描的包的 {@link String}{@code []}。
     */
    @Forward(annotation = ScanPackages.class, property = "value")
    String[] scanPackages() default {};

    /**
     * 获取测试数据源兼容模式。
     *
     * @return 表示数据源兼容模式的 {@link DatabaseModel}。
     */
    @Forward(annotation = EnableDataSource.class, property = "model")
    DatabaseModel databaseModel() default DatabaseModel.NONE;
}
