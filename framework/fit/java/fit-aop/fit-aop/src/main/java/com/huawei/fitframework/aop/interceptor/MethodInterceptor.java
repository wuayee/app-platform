/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor;

import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.inspection.Nullable;

/**
 * 方法拦截器，用于处理 AOP 的最基本接口。
 *
 * @author 季聿阶 j00559309
 * @since 2022-05-28
 */
public interface MethodInterceptor {
    /**
     * 获取方法拦截器所定义的方法切点集合。
     *
     * @return 表示方法切点集合的 {@link MethodPointcut}。
     */
    @Nonnull
    MethodPointcut getPointCut();

    /**
     * 执行拦截逻辑。
     *
     * @param methodJoinPoint 表示运行时的方法连接点的 {@link MethodJoinPoint}。
     * @return 表示调用拦截器后的返回值的 {@link Object}。
     * @throws Throwable 当抛出的异常为原方法所定义的异常类型或者是 {@link RuntimeException} 的子类时，需要直接抛出，
     * 否则，需要转换为 {@link AspectException} 抛出。
     */
    @Nullable
    Object intercept(@Nonnull MethodJoinPoint methodJoinPoint) throws Throwable;
}
