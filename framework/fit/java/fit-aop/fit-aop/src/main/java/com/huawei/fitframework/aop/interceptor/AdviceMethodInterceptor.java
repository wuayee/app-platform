/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor;

import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.inspection.Nullable;

import java.lang.reflect.Method;

/**
 * 带建议的方法拦截器。
 *
 * @author 季聿阶
 * @since 2022-05-05
 */
public interface AdviceMethodInterceptor extends MethodInterceptor {
    /**
     * 获取调用建议的对象。
     *
     * @return 表示调用建议的对象的 {@link Object}，如果是静态方法调用，则为 {@code null}。
     */
    @Nullable
    Object getAdvisorTarget();

    /**
     * 获取调用建议的方法。
     *
     * @return 表示调用建议的方法的 {@link Method}。
     */
    @Nonnull
    Method getAdvisorMethod();
}
