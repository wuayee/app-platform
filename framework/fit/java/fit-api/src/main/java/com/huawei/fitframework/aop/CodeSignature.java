/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.aop;

/**
 * 代码签名接口。
 *
 * @author 白鹏坤
 * @since 2023-04-12
 */
public interface CodeSignature extends Signature {
    /**
     * 获取调用方法的参数列表。
     *
     * @return 表示调用方法的参数列表的 {@link Class}{@code []}。
     */
    Class<?>[] getParameterTypes();

    /**
     * 获取调用方法的参数名数组。
     *
     * @return 表示调用方法的参数名称列表的 {@link String}{@code []}。
     */
    String[] getParameterNames();

    /**
     * 获取调用方法的异常类型。
     *
     * @return 表示调用方法的常类型的 {@link Class}。
     */
    Class<?>[] getExceptionTypes();
}
