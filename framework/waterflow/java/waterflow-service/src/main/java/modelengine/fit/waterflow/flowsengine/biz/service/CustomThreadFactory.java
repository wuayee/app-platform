/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.biz.service;

import modelengine.fitframework.log.Logger;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义的线程工厂
 *
 * @author 夏斐
 * @since 2024/3/1
 */
public class CustomThreadFactory implements ThreadFactory {
    private static final Logger LOG = Logger.get(CustomThreadFactory.class);

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final String namePrefix;

    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public CustomThreadFactory(String namePrefix) {
        this(namePrefix, null);
    }

    public CustomThreadFactory(String namePrefix, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.namePrefix = namePrefix;
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }

    /**
     * 新建一个thread
     *
     * @param runner a runnable to be executed by new thread instance
     * @return 新建的thread
     */
    public Thread newThread(Runnable runner) {
        Thread thread = new Thread(runner, namePrefix + "-" + threadNumber.getAndIncrement());
        if (thread.isDaemon()) {
            thread.setDaemon(false);
        }
        if (this.uncaughtExceptionHandler != null) {
            thread.setUncaughtExceptionHandler(this.uncaughtExceptionHandler);
        } else {
            thread.setUncaughtExceptionHandler((tr, ex) -> LOG.error(tr.getName() + " : " + ex.getMessage()));
        }
        return thread;
    }
}

