/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.http.code;

/**
 * 表示错误码接口类型。
 *
 * @author 易文渊
 * @since 2024-07-18
 */
public interface RetCode {
    /**
     * 获取错误码。
     *
     * @return 表示状态码的 {@code int}。
     */
    int getCode();

    /**
     * 获取错误信息。
     *
     * @return 表示错误信息的 {@link String}。
     */
    String getMsg();
}