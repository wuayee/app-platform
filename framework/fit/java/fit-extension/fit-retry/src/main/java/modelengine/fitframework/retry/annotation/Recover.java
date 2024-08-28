/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fitframework.retry.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示重试恢复方法的注解。
 * <p>其中恢复方法的第一个参数为异常类型，方法的其余参数和返回值与相应 {@link Retryable} 方法定义的参数、返回值一致。</p>
 *
 * @author 邬涨财
 * @since 2023-02-21
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Recover {}
