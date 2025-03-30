/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

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
