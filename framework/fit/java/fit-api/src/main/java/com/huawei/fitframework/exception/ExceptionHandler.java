/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.exception;

import java.lang.reflect.Method;

/**
 * 表示异常处理器。
 *
 * @author 季聿阶
 * @since 2022-11-11
 */
@FunctionalInterface
public interface ExceptionHandler {
    /**
     * 处理发生的异常。
     *
     * @param cause 表示发生异常的原因的 {@link Throwable}。
     * @param method 表示发生异常的方法的 {@link Method}。
     * @param params 表示发生异常的参数列表的 {@link Object}{@code []}。
     */
    void handleException(Throwable cause, Method method, Object... params);
}
