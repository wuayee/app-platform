/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.ioc.annotation;

/**
 * 当注解定义错误时引发的异常。
 *
 * @author 梁济时 l00815032
 * @since 2022-05-03
 */
public class AnnotationDefinitionException extends RuntimeException {
    /**
     * 使用异常信息初始化 {@link AnnotationDefinitionException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public AnnotationDefinitionException(String message) {
        super(message);
    }
}
