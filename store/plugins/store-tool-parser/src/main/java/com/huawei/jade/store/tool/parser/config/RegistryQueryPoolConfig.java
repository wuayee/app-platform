/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.config;

import modelengine.fitframework.annotation.AcceptConfigValues;
import modelengine.fitframework.annotation.Component;

/**
 * 注册中心查询线程池配置参数。
 *
 * @author 罗帅
 * @since 2024-8-19
 */
@Component
@AcceptConfigValues("store.registry.query.pool")
public class RegistryQueryPoolConfig {
    /** 核心线程数 */
    private int corePoolSize;

    /** 最大线程数 */
    private int maximumPoolSize;

    /** 等待队列大小 */
    private int workQueueCapacity;

    /**
     * 获取核心线程数。
     *
     * @return 表示核心线程数的 {@code int}。
     */
    public int getCorePoolSize() {
        return this.corePoolSize;
    }

    /**
     * 设置核心线程数。
     *
     * @param corePoolSize 表示获取核心线程数的 {@code int}。
     */
    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    /**
     * 获取最大线程数。
     *
     * @return 表示最大线程数的 {@code int}。
     */
    public int getMaximumPoolSize() {
        return this.maximumPoolSize;
    }

    /**
     * 设置最大线程数。
     *
     * @param maximumPoolSize 表示最大线程数的 {@code int}。
     */
    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    /**
     * 获取等待队列大小。
     *
     * @return 表示等待队列大小的 {@code int}。
     */
    public int getWorkQueueCapacity() {
        return this.workQueueCapacity;
    }

    /**
     * 设置等待队列大小。
     *
     * @param workQueueCapacity 表示等待队列大小的 {@code int}。
     */
    public void setWorkQueueCapacity(int workQueueCapacity) {
        this.workQueueCapacity = workQueueCapacity;
    }
}