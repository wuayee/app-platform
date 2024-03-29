/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.schedule.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.schedule.ExecutePolicy;
import com.huawei.fitframework.schedule.Task;
import com.huawei.fitframework.util.ObjectUtils;

import java.util.concurrent.Callable;

/**
 * 表示 {@link Task} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2022-11-16
 */
public class DefaultTask implements Task {
    private final Callable<?> callable;
    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    private final ExecutePolicy policy;

    private DefaultTask(Callable<?> callable, Thread.UncaughtExceptionHandler uncaughtExceptionHandler,
            ExecutePolicy policy) {
        this.callable = notNull(callable, "The callable cannot be null.");
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        this.policy = ObjectUtils.getIfNull(policy, ExecutePolicy::disposable);
    }

    @Override
    public Object call() throws Exception {
        try {
            return this.callable.call();
        } catch (Exception e) {
            if (this.uncaughtExceptionHandler == null) {
                throw e;
            }
            this.uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e);
            return null;
        }
    }

    @Override
    public Thread.UncaughtExceptionHandler uncaughtExceptionHandler() {
        return this.uncaughtExceptionHandler;
    }

    @Override
    public ExecutePolicy policy() {
        return this.policy;
    }

    public static class DefaultDisposableTask extends DefaultTask implements DisposableTask {
        private DefaultDisposableTask(Callable<?> callable, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
            super(callable, uncaughtExceptionHandler, ExecutePolicy.disposable());
        }
    }

    /**
     * 表示 {@link Task.Builder} 的默认实现。
     */
    public static class Builder implements Task.Builder {
        private Callable<?> callable;
        private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
        private ExecutePolicy policy;

        public Builder(Task task) {
            if (task != null) {
                this.callable = task;
                this.uncaughtExceptionHandler = task.uncaughtExceptionHandler();
                this.policy = task.policy();
            }
        }

        @Override
        public Task.Builder runnable(Runnable runnable) {
            if (runnable == null) {
                return this;
            }
            this.callable = () -> {
                runnable.run();
                return null;
            };
            return this;
        }

        @Override
        public Task.Builder callable(Callable<?> callable) {
            this.callable = callable;
            return this;
        }

        @Override
        public Task.Builder uncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
            this.uncaughtExceptionHandler = handler;
            return this;
        }

        @Override
        public Task.Builder policy(ExecutePolicy policy) {
            this.policy = policy;
            return this;
        }

        @Override
        public Task build() {
            return new DefaultTask(this.callable, this.uncaughtExceptionHandler, this.policy);
        }

        @Override
        public DisposableTask buildDisposable() {
            return new DefaultDisposableTask(this.callable, this.uncaughtExceptionHandler);
        }
    }
}
