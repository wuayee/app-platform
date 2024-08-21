/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fitframework.ioc.annotation.circular;

/**
 * 为循环转发的注解提供消费方。
 *
 * @author 梁济时
 * @since 2022-05-31
 */
@Circular(key1 = CircularAnnotationConsumer.VALUE)
public class CircularAnnotationConsumer {
    /**
     * 表示循环转发的值。
     */
    public static final String VALUE = "CircularAnnotationConsumer";
}
