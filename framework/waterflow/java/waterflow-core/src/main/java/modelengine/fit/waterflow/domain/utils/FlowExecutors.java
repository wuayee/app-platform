/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.utils;

import static java.util.concurrent.TimeUnit.SECONDS;

import modelengine.fitframework.log.Logger;
import modelengine.fitframework.schedule.ThreadPoolExecutor;

import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 流程引擎的节点线程池
 *
 * @author 高诗意
 * @since 1.0
 */
public final class FlowExecutors {
    private static final int MAX_THREAD_COUNT = 64;

    private static final int CORE_THREAD_COUNT = 8;

    private static final Logger LOG = Logger.get(FlowExecutors.class);

    private static final ThreadPoolExecutor THREAD_POOL;

    private static AtomicInteger currentConcurrency = new AtomicInteger(0);

    static {
        ThreadPoolExecutor newPool = ThreadPoolExecutor.custom()
                .threadPoolName("flow-node-thread-pool")
                .corePoolSize(CORE_THREAD_COUNT)
                .maximumPoolSize(MAX_THREAD_COUNT)
                .workQueueCapacity(0)
                .keepAliveTime(60L, SECONDS)
                .exceptionHandler((thread, throwable) -> {
                    LOG.error("The node pool run failed, error cause: {}, message: {}.", throwable.getCause(),
                            throwable.getMessage());
                    LOG.debug("The node pool run failed details: ", throwable);
                })
                .rejectedExecutionHandler(new AbortPolicy())
                .build();
        THREAD_POOL = newPool;
    }

    /**
     * 获取执行线程池
     *
     * @return 线程池对象
     */
    public static ThreadPoolExecutor getThreadPool() {
        return THREAD_POOL;
    }

    /**
     * 并发持有对象
     *
     * @author s00558940
     * @since 2024/8/12
     */
    public static class ConcurrencyHolder {
        private boolean isReleased;

        private ConcurrencyHolder() {
            this.isReleased = false;
        }

        /**
         * 释放并发
         */
        public void release() {
            if (!this.isReleased) {
                this.isReleased = true;
                FlowExecutors.decrementConcurrency();
            }
        }
    }

    /**
     * 增加一个并发
     *
     * @return 并发持有对象。如果无法增加并发则返回null对象
     */
    public static synchronized Optional<ConcurrencyHolder> incrementConcurrency() {
        if (currentConcurrency.incrementAndGet() > MAX_THREAD_COUNT) {
            currentConcurrency.decrementAndGet();
            return Optional.empty();
        }
        return Optional.of(new ConcurrencyHolder());
    }

    private static synchronized void decrementConcurrency() {
        currentConcurrency.decrementAndGet();
    }
}
