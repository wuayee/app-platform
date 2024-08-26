/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fit.server;

/**
 * 启动服务器异常。
 *
 * @author 季聿阶
 * @since 2022-09-16
 */
public class StartServerException extends RuntimeException {
    /**
     * 使用异常信息和引发异常的原因初始化 {@link StartServerException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public StartServerException(String message) {
        super(message);
    }

    /**
     * 使用异常信息和引发异常的原因初始化 {@link StartServerException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public StartServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
