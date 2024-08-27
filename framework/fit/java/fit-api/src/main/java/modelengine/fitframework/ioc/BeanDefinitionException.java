/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fitframework.ioc;

/**
 * 当Bean的定义不正确时引发的异常。
 *
 * @author 梁济时
 * @since 2022-04-28
 */
public class BeanDefinitionException extends IocException {
    /**
     * 使用异常信息初始化 {@link BeanDefinitionException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public BeanDefinitionException(String message) {
        super(message);
    }

    /**
     * 使用引发异常的原因初始化 {@link BeanDefinitionException} 类的新实例。
     *
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public BeanDefinitionException(Throwable cause) {
        super(cause);
    }

    /**
     * 使用异常信息和引发异常的原因初始化 {@link BeanDefinitionException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public BeanDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }
}
