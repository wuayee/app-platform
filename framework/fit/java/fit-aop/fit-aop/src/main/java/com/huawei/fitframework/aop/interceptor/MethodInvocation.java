/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor;

import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.inspection.Nullable;

import java.lang.reflect.Method;

/**
 * 表示一个可执行的方法。
 *
 * @author 季聿阶
 * @since 2022-05-17
 */
public interface MethodInvocation {
    /**
     * 获取调用方法的对象，如果是静态方法，则为 {@code null}。
     *
     * @return 表示调用方法的对象的 {@link Object}，如果是静态方法，则为 {@code null}。
     */
    @Nullable
    Object getTarget();

    /**
     * 获取方法定义。
     *
     * @return 表示方法定义的 {@link Method}。
     */
    @Nonnull
    Method getMethod();

    /**
     * 获取调用方法的参数列表。
     *
     * @return 表示调用方法的参数列表的 {@link Object}{@code []}。
     */
    @Nonnull
    Object[] getArguments();

    /**
     * 设置调用方法的参数列表。
     *
     * @param arguments 表示待设置的参数列表的 {@link Object}{@code []}。
     */
    void setArguments(@Nonnull Object[] arguments);
}
