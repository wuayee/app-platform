/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.base.common;

/**
 * 错误码
 *
 * @since 2024-5-27
 *
 */
public interface ErrorCode {
    /**
     * 获取错误码
     *
     * @return 错误码
     */
    int getErrorCode();

    /**
     * 获取错误提示信息
     *
     * @return 错误信息
     */
    String getMessage();
}
