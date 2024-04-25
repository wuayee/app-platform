/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.utils;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.schedule.ThreadPoolExecutor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.stream.Collectors;

/**
 * 流程引擎的节点线程池
 *
 * @author 00564732
 * @since 1.0
 */
public final class FlowExecutors {
    private static final Logger log = Logger.get(FlowExecutors.class);

    private static final Map<String, ThreadPoolExecutor> THREAD_POOLS = new ConcurrentHashMap<>();

    /**
     * 获取节点线程池，节点一次只有一个线程在处理，因此无需加锁
     * 如果要改为一个流程定义一个线程池，则只需要更新key值
     *
     * @param key 有流程版本和节点ID组成的key（streamID+nodeID），每个节点一个线程池
     * @param parallelNum 节点的并发度，默认为10，后续支持配置
     * @return 线程池对象
     */
    public static ThreadPoolExecutor getThreadPool(String key, int parallelNum) {
        ThreadPoolExecutor exits = THREAD_POOLS.get(key);
        if (exits != null) {
            return exits;
        }
        ThreadPoolExecutor newPool = ThreadPoolExecutor.custom()
                .threadPoolName("flow-node-thread-pool")
                .corePoolSize(0)
                .maximumPoolSize(parallelNum)
                .workQueueCapacity(10)
                .keepAliveTime(60L, SECONDS)
                .exceptionHandler((thread, throwable) -> {
                    log.error("[node-pool-{}]:  The node pool run failed, error cause: {}, message: {}.", key,
                            throwable.getCause(), throwable.getMessage());
                    log.error("The node pool run failed details: ", throwable);
                })
                .rejectedExecutionHandler(new AbortPolicy())
                .build();
        THREAD_POOLS.putIfAbsent(key, newPool);
        return newPool;
    }

    /**
     * 删除流程版本所有节点的线程池
     * TODO xiangyu 删除流程定义的时候，需要将该定义对应的节点的线程池全部删除
     *
     * @param key 流程版本，streamID
     */
    public static void removeThreadPool(String key) {
        Set<String> keysToRemove = THREAD_POOLS.keySet()
                .stream()
                .filter(k -> k.startsWith(key))
                .collect(Collectors.toSet());
        keysToRemove.forEach(k -> {
            try {
                THREAD_POOLS.get(k).shutdown();
            } catch (InterruptedException e) {
                log.error("[node-pool-{}]: The pool shutdown cause InterruptedException, error cause: {}, message: {}.",
                        k, e.getCause(), e.getMessage());
            } finally {
                THREAD_POOLS.remove(k);
            }
        });
    }
}
