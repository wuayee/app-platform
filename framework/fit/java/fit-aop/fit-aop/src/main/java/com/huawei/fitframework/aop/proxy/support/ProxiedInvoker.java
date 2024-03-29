/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.proxy.support;

import com.huawei.fitframework.aop.interceptor.MethodInvocation;

/**
 * 表示调用被代理对象的方法。
 *
 * @author 季聿阶 j00559309
 * @since 2022-05-25
 */
@FunctionalInterface
public interface ProxiedInvoker {
    /**
     * 通过调用信息来调用被代理对象。
     *
     * @param invocation 表示调用信息的 {@link MethodInvocation}。
     * @return 表示调用的返回值的 {@link Object}。
     * @throws Throwable 当调用过程中发生异常时。
     */
    Object invoke(MethodInvocation invocation) throws Throwable;
}
