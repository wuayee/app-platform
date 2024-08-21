/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.config;

import modelengine.fitframework.annotation.Bean;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.thread.DefaultThreadFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Asynchronous注解所使用线程池配置
 *
 * @author 陈镕希
 * @since 2023-06-27
 */
@Component
public class EventHandlerAsyncConfigurer {
    /**
     * getExecutor
     *
     * @return Executor
     */
    @Bean
    public Executor getExecutor() {
        return new ThreadPoolExecutor(5, 10, 1000, TimeUnit.SECONDS, this.createWorkQueue(),
                new DefaultThreadFactory("Event-Handler", false, (thread, throwable) -> {}),
                new java.util.concurrent.ThreadPoolExecutor.AbortPolicy());
    }

    private BlockingQueue<Runnable> createWorkQueue() {
        return new LinkedBlockingQueue<>(1000);
    }
}
