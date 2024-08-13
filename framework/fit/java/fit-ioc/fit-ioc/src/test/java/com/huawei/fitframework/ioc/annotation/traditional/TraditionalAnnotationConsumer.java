/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.ioc.annotation.traditional;

/**
 * 为传统注解提供消费方。
 *
 * @author 梁济时
 * @since 2022-05-31
 */
@FirstLevel(TraditionalAnnotationConsumer.VALUE)
public class TraditionalAnnotationConsumer {
    /**
     * 表示注解的值。
     */
    public static final String VALUE = "Traditional";
}
