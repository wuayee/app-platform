/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fitframework.ioc;

/**
 * 当解析异常失败时引发的异常。
 *
 * @author 梁济时
 * @since 2022-08-05
 */
public class UnresolvableDependencyException extends DependencyException {
    /**
     * 使用异常信息初始化 {@link UnresolvableDependencyException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public UnresolvableDependencyException(String message) {
        super(message);
    }

    /**
     * 使用引发异常的原因初始化 {@link UnresolvableDependencyException} 类的新实例。
     *
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public UnresolvableDependencyException(Throwable cause) {
        super(cause);
    }

    /**
     * 使用异常信息和引发异常的原因初始化 {@link UnresolvableDependencyException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public UnresolvableDependencyException(String message, Throwable cause) {
        super(message, cause);
    }
}
