/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.common.utils;

import modelengine.fitframework.schedule.ThreadPoolScheduler;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 全局异常处理工具类
 *
 * @author 晏钰坤
 * @since 2023/6/12
 */
public class GlobalExecutorUtil {
    private static final GlobalExecutorUtil INSTANCE = new GlobalExecutorUtil();

    private final ThreadPoolScheduler schedulerPool = ThreadPoolScheduler.custom()
            .threadPoolName("task-threadPool")
            .corePoolSize(5)
            .maximumPoolSize(10)
            .workQueueCapacity(1000)
            .rejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy())
            .build();

    public static GlobalExecutorUtil getInstance() {
        return INSTANCE;
    }

    public ThreadPoolScheduler getSchedulerPool() {
        return schedulerPool;
    }
}
