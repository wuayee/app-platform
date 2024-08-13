/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 */

package com.huawei.fitframework.util;

/**
 * 定义常见的换行符。
 *
 * @author 梁济时
 * @since 2021-11-05
 */
public enum LineSeparator {
    /**
     * 经典MacOS中使用的换行符。
     */
    CR("\r"),

    /**
     * 常规UNIX系统中使用的换行符。
     */
    LF("\n"),

    /**
     * Windows系统中使用的换行符。
     */
    CRLF("\r\n");

    private final String value;

    LineSeparator(String value) {
        this.value = value;
    }

    /**
     * 获取换行符的值。
     *
     * @return 表示换行符的值的 {@link String}。
     */
    public final String value() {
        return this.value;
    }
}
