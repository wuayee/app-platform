/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 */

package com.huawei.fitframework.exception;

/**
 * 当访问类型发生异常时引发的异常。
 *
 * @author 梁济时
 * @since 1.0
 */
public class ClassAccessException extends RuntimeException {
    /**
     * 使用引发该异常的原因初始化 {@link ClassAccessException} 类的新实例。
     *
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public ClassAccessException(Throwable cause) {
        super(cause);
    }
}
