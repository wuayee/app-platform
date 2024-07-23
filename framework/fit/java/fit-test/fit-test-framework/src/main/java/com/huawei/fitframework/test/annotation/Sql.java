/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于在测试用例前执行初始化 Sql 语句。
 *
 * @author 易文渊
 * @since 2024-07-21
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Sql {
    /**
     * 获取 sql 脚本文件路径。
     *
     * @return 表示 sql 脚本文件路径集合的 {@code String[]}。
     */
    String[] scripts();
}
