/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.data.repository.support;

/**
 * 表示本地未查询到指定缓存数据时抛出的异常。
 *
 * @author 王成 w00863339
 * @since 2024/7/8
 */
public class CachedDataNotFoundException extends IllegalStateException {
    /**
     * 使用异常信息初始化 {@link CachedDataNotFoundException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public CachedDataNotFoundException(String message) {
        super(message);
    }

    /**
     * 使用异常信息和引发异常的原因初始化 {@link CachedDataNotFoundException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public CachedDataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}