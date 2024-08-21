/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.edatamate.service;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.schedule.annotation.Scheduled;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * 定时任务监测内存使用率
 *
 * @author 孙怡菲
 * @since 2024-02-28
 */
@Component
public class ResourceMonitor {
    private static final Logger log = Logger.get(ResourceMonitor.class);

    private static final double MEMORY_THRESHOLD = 0.8D; // 80%

    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

    /**
     * getMemoryAUsage
     */
    @Scheduled(strategy = Scheduled.Strategy.FIXED_RATE, value = "3000")
    public void getMemoryAUsage() {
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        double heapMemoryUtilization = (double) heapMemoryUsage.getUsed() / heapMemoryUsage.getMax();

        if (heapMemoryUtilization > MEMORY_THRESHOLD) {
            log.warn("Heap memory usage exceeds threshold: {}%", heapMemoryUtilization * 100);
        }
    }
}
