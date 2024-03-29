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
 * @author gwx900499
 * @since 2023-01-04
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AssignmentTarget {
    byte byteValue() default 2;

    char charValue() default 'a';

    double doubleValue() default 0.0;

    float floatValue() default 0;

    int integerValue () default 0;

    long longValue () default 0;

    short shortValue () default 0;

    boolean booleanValue () default false;

    String stringValue () default "";

    enum EnumValue {BLUE, RED, GREEN}

    EnumValue fruitColor() default EnumValue.GREEN;

    String[] arrayValue() default {};

    Class classValue();

    NestAssignmentTarget annotationValue();
}
