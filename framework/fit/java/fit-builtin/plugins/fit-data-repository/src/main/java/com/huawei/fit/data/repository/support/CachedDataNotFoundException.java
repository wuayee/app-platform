/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.data.repository.support;

/**
 * 表示本地未查询到指定缓存数据时抛出的异常。
 *
 * @author 王成 w00863339
 * @since 2024/7/8
 */
public class CachedDataNotFoundException extends IllegalStateException {
    /**
     * 使用异常信息初始化 {@link com.huawei.fit.data.repository.support.CachedDataNotFoundException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public CachedDataNotFoundException(String message) {
        super(message);
    }

    /**
     * 使用异常信息和引发异常的原因初始化 {@link com.huawei.fit.data.repository.support.CachedDataNotFoundException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public CachedDataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}