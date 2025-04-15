/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.aop;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 字段值国际化注解
 *
 * @author 陈潇文
 * @since 2024-08-19
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface Locale {}
