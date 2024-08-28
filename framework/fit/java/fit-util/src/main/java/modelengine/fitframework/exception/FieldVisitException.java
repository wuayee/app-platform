/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 */

package modelengine.fitframework.exception;

/**
 * 当访问字段失败时引发的异常。
 *
 * @author 梁济时
 * @since 1.0
 */
public class FieldVisitException extends RuntimeException {
    /**
     * 使用引发异常的原因初始化 {@link FieldVisitException} 类的新实例。
     *
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public FieldVisitException(Throwable cause) {
        super(cause);
    }
}
