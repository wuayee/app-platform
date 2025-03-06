/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.oms.entity;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 表示刷新信息的类。
 *
 * @author 李金绪
 * @since 2024-12-10
 */
public class ServiceInfo {
    private List<Instance> instances;
    private AtomicInteger curOmsIndex;
    private volatile long lastRefreshTime;

    /**
     * 构造函数。
     *
     * @param instances 表示实例集合的 {@link List}{@code <}{@link Instance}{@code >}。
     */
    public ServiceInfo(List<Instance> instances) {
        this.instances = instances;
        this.curOmsIndex = new AtomicInteger(-1);
        this.lastRefreshTime = -1L;
    }

    /**
     * 获取实例集合。
     *
     * @return 表示实例集合的 {@link List}{@code <}{@link Instance}{@code >}。
     */
    public List<Instance> getInstances() {
        return this.instances;
    }

    /**
     * 设置实例集合。
     *
     * @param instances 表示实例集合的 {@link List}{@code <}{@link Instance}{@code >}。
     */
    public void setInstances(List<Instance> instances) {
        this.instances = instances;
    }

    /**
     * 获取索引。
     *
     * @return 表示索引的 {@link AtomicInteger}。
     */
    public AtomicInteger getCurOmsIndex() {
        return this.curOmsIndex;
    }

    /**
     * 设备索引。
     *
     * @param curOmsIndex 表示索引的 {@link AtomicInteger}。
     */
    public void setCurOmsIndex(AtomicInteger curOmsIndex) {
        this.curOmsIndex = curOmsIndex;
    }

    /**
     * 获取最后刷新时间。
     *
     * @return 表示最后刷新时间的 {@code long}。
     */
    public long getLastRefreshTime() {
        return this.lastRefreshTime;
    }

    /**
     * 设置最后刷新时间。
     *
     * @param lastRefreshTime 表示最后刷新时间的 {@code long}。
     */
    public void setLastRefreshTime(long lastRefreshTime) {
        this.lastRefreshTime = lastRefreshTime;
    }
}
