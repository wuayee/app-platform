/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 */

package com.huawei.fitframework.exception;

/**
 * 当发现日期格式错误时引发的异常。
 *
 * @author 梁济时 l00815032
 * @since 1.0
 */
public class DateFormatException extends IllegalArgumentException {
    /**
     * 使用引发该异常的原因初始化 {@link DateFormatException} 类的新实例。
     *
     * @param cause 表示引发该异常的原因的 {@link Throwable}。
     */
    public DateFormatException(Throwable cause) {
        super(cause);
    }
}
