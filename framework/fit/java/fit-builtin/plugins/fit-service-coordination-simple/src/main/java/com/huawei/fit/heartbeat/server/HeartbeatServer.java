/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.heartbeat.server;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.heartbeat.HeartbeatService;
import com.huawei.fit.service.WorkerCache;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 用于提供心跳相关的服务。
 *
 * @author 邬涨财 w00575064
 * @since 2022-04-11
 */
@Component
public class HeartbeatServer implements HeartbeatService {
    private final WorkerCache cache;

    public HeartbeatServer(WorkerCache cache) {
        this.cache = notNull(cache, "The worker cache cannot be null.");
    }

    @Override
    @Fitable(id = "simple")
    public Boolean sendHeartbeat(List<HeartbeatInfo> beatInfos, Address address) {
        if (address == null || StringUtils.isBlank(address.getId())) {
            return false;
        }
        if (CollectionUtils.isEmpty(beatInfos)) {
            return false;
        }
        HeartbeatInfo first = beatInfos.get(0);
        if (first == null || first.getAliveTime() == null || first.getAliveTime() <= 0) {
            return false;
        }
        Instant expireTime = Instant.now().plus(first.getAliveTime(), ChronoUnit.MILLIS);
        this.cache.refreshWorker(address.getId(), expireTime);
        return true;
    }

    @Override
    public Boolean stopHeartbeat(List<HeartbeatInfo> heartbeatInfo, Address address) {
        return true;
    }
}
