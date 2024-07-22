/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.test.annotation;

import com.huawei.fitframework.test.domain.db.DataBaseModelEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于测试框架注入数据源。
 *
 * @author 易文渊
 * @see com.huawei.fitframework.test.domain.listener.DataSourceListener
 * @since 2024-07-21
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableDataSource {
    /**
     * 获取测试数据源兼容模式。
     *
     * @return 表示数据源兼容模式的 {@link DataBaseModelEnum}。
     */
    DataBaseModelEnum model() default DataBaseModelEnum.NONE;
}
