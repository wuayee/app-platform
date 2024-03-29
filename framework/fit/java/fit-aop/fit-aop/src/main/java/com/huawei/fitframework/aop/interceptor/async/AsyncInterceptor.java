/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.async;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.aop.interceptor.MethodJoinPoint;
import com.huawei.fitframework.aop.interceptor.support.AbstractMethodInterceptor;
import com.huawei.fitframework.exception.ExceptionHandler;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.inspection.Nullable;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.LazyLoader;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * 表示异步执行的方法拦截器。
 *
 * @author 季聿阶 j00559309
 * @since 2022-11-11
 */
public class AsyncInterceptor extends AbstractMethodInterceptor {
    private static final Logger log = Logger.get(AsyncInterceptor.class);

    private final LazyLoader<Executor> executorLoader;
    private final LazyLoader<ExceptionHandler> exceptionHandlerLoader;

    public AsyncInterceptor(Supplier<Executor> executorSupplier, Supplier<ExceptionHandler> exceptionHandlerSupplier) {
        this.executorLoader =
                new LazyLoader<>(notNull(executorSupplier, "The async executor supplier cannot be null."));
        this.exceptionHandlerLoader = new LazyLoader<>(exceptionHandlerSupplier);
    }

    @Nullable
    @Override
    public Object intercept(@Nonnull MethodJoinPoint methodJoinPoint) throws Throwable {
        this.executorLoader.get().execute(() -> this.asyncExecute(methodJoinPoint));
        return null;
    }

    private void asyncExecute(MethodJoinPoint methodJoinPoint) {
        try {
            methodJoinPoint.proceed();
        } catch (Throwable e) {
            ExceptionHandler exceptionHandler = this.exceptionHandlerLoader.get();
            if (exceptionHandler == null) {
                log.debug(e.getClass().getName(), e);
                log.error("Failed to execute asynchronously. [class={}, method={}]",
                        methodJoinPoint.getProxiedInvocation().getTarget() == null
                                ? null
                                : methodJoinPoint.getProxiedInvocation().getTarget().getClass().getName(),
                        methodJoinPoint.getProxiedInvocation().getMethod().getName());
            } else {
                exceptionHandler.handleException(e,
                        methodJoinPoint.getProxiedInvocation().getMethod(),
                        methodJoinPoint.getProxiedInvocation().getArguments());
            }
        }
    }
}
