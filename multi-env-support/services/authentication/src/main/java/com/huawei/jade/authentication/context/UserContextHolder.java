/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.authentication.context;

/**
 * 表示当前线程持有当前Http请求的操作信息。
 *
 * @author 陈潇文
 * @since 2024-07-31
 */
public class UserContextHolder {
    private static final ThreadLocal<UserContext> OPERATION_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 获取本地线程变量的操作信息。
     *
     * @return 表示当前操作信息的 {@link UserContext}。
     */
    public static UserContext get() {
        return OPERATION_CONTEXT_THREAD_LOCAL.get();
    }

    /**
     * 将当前操作信息塞入本地线程变量中。
     *
     * @param operationContext 表示当前操作信息的 {@link UserContext}。
     * @param action 表示执行的任务 {@link Action}。
     */
    public static void apply(UserContext operationContext, Action action) {
        OPERATION_CONTEXT_THREAD_LOCAL.set(operationContext);
        try {
            action.exec();
        } finally {
            OPERATION_CONTEXT_THREAD_LOCAL.remove();
        }
    }

    /**
     * 任务执行接口。
     */
    public interface Action {
        /**
         * 执行任务。
         */
        void exec();
    }
}
