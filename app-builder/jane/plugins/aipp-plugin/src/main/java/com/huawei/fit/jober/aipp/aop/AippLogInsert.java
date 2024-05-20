package com.huawei.fit.jober.aipp.aop;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 日志插入注解.
 *
 * @author z00559346 张越
 * @since 2024-05-15
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface AippLogInsert {}
