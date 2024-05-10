/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.entity;

import com.huawei.fit.jober.aipp.service.DistributedMapService;
import com.huawei.fitframework.log.Logger;

import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 大模型流式响应监听器
 *
 * @author l00611472
 * @since 2024/02/06
 */
public class LlmEventListener extends EventSourceListener {
    private static final Logger log = Logger.get(LlmEventListener.class);
    private static final long EXPIRE_MILLISECONDS = 1800000L; // 30 min
    private static final long CACHE_UPDATE_TASK_PERIOD = 1000L;
    private final String instanceId;
    private final String modelResultKey;
    private final DistributedMapService mapService;
    private final StringBuilder ssb;
    private final CountDownLatch countDownLatch;
    private long lastTime;

    public LlmEventListener(String instanceId, String modelResultKey, DistributedMapService mapService) {
        this.instanceId = instanceId;
        this.modelResultKey = modelResultKey;
        this.mapService = mapService;
        this.ssb = new StringBuilder();
        this.countDownLatch = new CountDownLatch(1);
        this.lastTime = System.currentTimeMillis();
    }

    @Override
    public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
        mapService.put(instanceId, modelResultKey, "");
        if (!mapService.expire(instanceId, EXPIRE_MILLISECONDS)) {
            log.warn("expire cache failed, instanceId {}", instanceId);
        }
        lastTime = System.currentTimeMillis();
    }

    @Override
    public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type,
            @NotNull String data) {
        ssb.append(data);
        long currTime = System.currentTimeMillis();
        if (currTime - lastTime >= CACHE_UPDATE_TASK_PERIOD) {
            mapService.put(instanceId, modelResultKey, ssb.toString());
            lastTime = currTime;
        }
    }

    @Override
    public void onClosed(@NotNull EventSource eventSource) {
        log.info("onClosed, instanceId: {}", instanceId);
        this.close();
    }

    @Override
    public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
        log.error("onFailure, instanceId: {}", instanceId);
        this.close();
        eventSource.cancel();
    }

    private void close() {
        mapService.put(instanceId, modelResultKey, ssb.toString());
        countDownLatch.countDown();
    }

    public boolean await(long timeout, TimeUnit unit) {
        try {
            return this.countDownLatch.await(timeout, unit);
        } catch (InterruptedException e) {
            log.error("LlmEventListener await timeout, instanceId {}.", instanceId);
        }
        return false;
    }
}
