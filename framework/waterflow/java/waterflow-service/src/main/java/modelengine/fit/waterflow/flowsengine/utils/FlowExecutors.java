/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.utils;

import modelengine.fitframework.log.Logger;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 流程引擎的节点线程池
 *
 * @author 高诗意
 * @since 2023/10/30
 */
public final class FlowExecutors {
    private static final Logger LOG = Logger.get(FlowExecutors.class);

    private static final Map<String, PriorityThreadPool> THREAD_POOLS = new ConcurrentHashMap<>();

    /**
     * 获取节点线程池，节点一次只有一个线程在处理，因此无需加锁
     * 如果要改为一个流程定义一个线程池，则只需要更新key值
     *
     * @param key 有流程版本和节点ID组成的key（streamID+nodeID），每个节点一个线程池
     * @param parallelNum 节点的并发度，默认为10，后续支持配置
     * @return 线程池对象
     */
    public static PriorityThreadPool getThreadPool(String key, int parallelNum) {
        String actualKey = "common";
        int actualParallelNum = 16;
        PriorityThreadPool exits = THREAD_POOLS.get(actualKey);
        if (exits != null) {
            return exits;
        }
        PriorityThreadPool threadPool = PriorityThreadPool.build(actualKey, actualParallelNum);
        THREAD_POOLS.putIfAbsent(actualKey, threadPool);
        return threadPool;
    }

    /**
     * 删除流程版本所有节点的线程池
     *
     * @param keyPrefix 流程版本，streamID
     */
    public static void removeThreadPool(String keyPrefix) {
        Set<String> keysToRemove = THREAD_POOLS.keySet()
                .stream()
                .filter(key -> key.startsWith(keyPrefix))
                .collect(Collectors.toSet());
        keysToRemove.forEach(key -> {
            try {
                THREAD_POOLS.get(key).shutdown();
            } finally {
                THREAD_POOLS.remove(key);
            }
        });
    }
}
