/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jane.common.response;

/**
 * 错误码接口
 *
 * @author 刘信宏
 * @since 2024-2-2
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
