/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package modelengine.fitframework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指示 Bean 仅在指定的 Profile 中生效。
 *
 * @author 梁济时
 * @since 2022-11-14
 */
@Conditional(ProfileCondition.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Profile {
    /**
     * 获取 Bean 生效的 Profile。
     *
     * @return 表示 Profile 的 {@link String}{@code []}。
     */
    String[] value() default {};
}
