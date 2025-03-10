/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.common.config;

import modelengine.fit.waterflow.common.Constant;
import modelengine.fitframework.annotation.Bean;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.thread.DefaultThreadFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 流程引擎异步处理线程池
 *
 * @author 晏钰坤
 * @since 2023/12/12
 */
@Component
public class FlowsEventHandlerAsyncConfigure {
    private static final Logger log = Logger.get(FlowsEventHandlerAsyncConfigure.class);

    /**
     * getExecutor
     *
     * @return Executor
     */
    @Bean(value = Constant.FLOWS_EVENT_HANDLER_EXECUTOR)
    public Executor getExecutor() {
        return new ThreadPoolExecutor(0, 10, 60L, TimeUnit.SECONDS, this.createWorkQueue(),
                new DefaultThreadFactory("flows-event-handler-thread-pool", false, (thread, throwable) -> {
                    log.error("[flows-event-handler-thread-pool]: The pool run failed, error cause: {}, message: {}.",
                            throwable.getCause(), throwable.getMessage());
                    log.error("The flows event handler pool run failed details: ", throwable);
                }), new java.util.concurrent.ThreadPoolExecutor.AbortPolicy());
    }

    private BlockingQueue<Runnable> createWorkQueue() {
        return new LinkedBlockingQueue<>(1000);
    }
}
