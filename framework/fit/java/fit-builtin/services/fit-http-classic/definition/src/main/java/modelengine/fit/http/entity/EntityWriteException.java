/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fit.http.entity;

/**
 * 表示消息体写回时发生的异常。
 *
 * @author 季聿阶
 * @since 2022-07-14
 */
public class EntityWriteException extends EntityException {
    /**
     * 通过异常消息来实例化 {@link EntityWriteException}。
     *
     * @param message 表示异常消息的 {@link String}。
     */
    public EntityWriteException(String message) {
        super(message);
    }

    /**
     * 通过异常消息和异常原因来实例化 {@link EntityWriteException}。
     *
     * @param message 表示异常消息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public EntityWriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
