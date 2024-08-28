/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fitframework.ioc.annotation.repeatable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示可重复注解的定义。
 *
 * @author 梁济时
 * @since 2022-05-31
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RepeatableValue.class)
public @interface Value {
    /**
     * 表示值的名称。
     *
     * @return 表示值的 {@link String}。
     */
    String value() default "";
}
