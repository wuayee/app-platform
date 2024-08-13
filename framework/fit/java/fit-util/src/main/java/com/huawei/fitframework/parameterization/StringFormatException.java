/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 */

package com.huawei.fitframework.parameterization;

/**
 * 当发现参数化字符串的格式错误，或参数未提供时引发的异常。
 *
 * @author 梁济时
 * @since 1.0
 */
public class StringFormatException extends IllegalArgumentException {
    /**
     * 使用异常信息初始化 {@link StringFormatException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public StringFormatException(String message) {
        super(message);
    }
}
