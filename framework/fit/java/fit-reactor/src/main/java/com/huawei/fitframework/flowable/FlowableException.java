/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable;

import com.huawei.fitframework.exception.ErrorCode;
import com.huawei.fitframework.exception.FitException;

/**
 * 表示响应式编程框架的基础异常。
 *
 * @author 季聿阶
 * @since 2024-02-09
 */
@ErrorCode(FlowableException.CODE)
public class FlowableException extends FitException {
    /** 表示响应式编程框架的根异常码。 */
    public static final int CODE = 0x7F060000;

    /**
     * 通过异常信息来初始化响应式编程框架的基础异常。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public FlowableException(String message) {
        super(message);
    }

    /**
     * 通过异常原因来初始化响应式编程框架的基础异常。
     *
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public FlowableException(Throwable cause) {
        super(cause);
    }

    /**
     * 通过异常信息和异常原因来初始化响应式编程框架的基础异常。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public FlowableException(String message, Throwable cause) {
        super(message, cause);
    }
}
