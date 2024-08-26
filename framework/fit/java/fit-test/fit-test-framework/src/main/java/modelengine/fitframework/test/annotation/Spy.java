/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fitframework.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于测试类字段的侦听。
 * <p>被侦听的字段可以部分自定义行为，部分保持原来的行为。</p>
 * <p>注意：该注解不能通过 {@link com.huawei.fitframework.annotation.Fit} 注解来进行测试字段的注入，因为 {@link Spied}
 * 注解需要被注解的类已经存在一个 Bean。</p>
 *
 * @author 季聿阶
 * @since 2024-07-27
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Spy {}
