/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fit.heartbeat.client.support;

import static com.huawei.fitframework.inspection.Validation.greaterThan;
import static com.huawei.fitframework.inspection.Validation.greaterThanOrEquals;
import static com.huawei.fitframework.inspection.Validation.notBlank;

import com.huawei.fit.heartbeat.client.HeartbeatInfo;

/**
 * 表示 {@link HeartbeatInfo} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-09-16
 */
public class DefaultHeartbeatInfo implements HeartbeatInfo {
    private final String scenario;
    private final int period;
    private final int initialExtraAliveTime;
    private final int aliveTime;
    private volatile long lastSyncTime;

    public DefaultHeartbeatInfo(String scenario, int period, int initialExtraAliveTime, int aliveTime) {
        this.scenario = notBlank(scenario, "The scenario to keep alive cannot be blank.");
        this.period = greaterThan(period, 0, "The period to keep alive must be positive.");
        this.initialExtraAliveTime =
                greaterThanOrEquals(initialExtraAliveTime, 0, "The initial extra alive time cannot be negative.");
        this.aliveTime = greaterThan(aliveTime, 0, "The alive time must be positive.");
    }

    @Override
    public String scenario() {
        return this.scenario;
    }

    @Override
    public int period() {
        return this.period;
    }

    @Override
    public int initialExtraAliveTime() {
        return this.initialExtraAliveTime;
    }

    @Override
    public int aliveTime() {
        return this.aliveTime;
    }

    @Override
    public long lastSyncTime() {
        return this.lastSyncTime;
    }

    @Override
    public void lastSyncTime(long lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }
}
