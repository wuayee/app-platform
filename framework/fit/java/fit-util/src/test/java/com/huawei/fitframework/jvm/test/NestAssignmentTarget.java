/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.jvm.test;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * jvm 模块测试文件
 *
 * @author 郭龙飞
 * @since 2023-01-04
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NestAssignmentTarget {
    int integerValue() default 0;
}
