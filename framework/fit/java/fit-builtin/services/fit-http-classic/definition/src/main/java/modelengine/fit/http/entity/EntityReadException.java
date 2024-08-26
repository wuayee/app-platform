/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fit.http.entity;

/**
 * 表示读取消息体时发生的异常。
 *
 * @author 季聿阶
 * @since 2022-07-14
 */
public class EntityReadException extends EntityException {
    /**
     * 通过异常消息来实例化 {@link EntityReadException}。
     *
     * @param message 表示异常消息的 {@link String}。
     */
    public EntityReadException(String message) {
        super(message);
    }

    /**
     * 通过异常消息和异常原因来实例化 {@link EntityReadException}。
     *
     * @param message 表示异常消息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public EntityReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
