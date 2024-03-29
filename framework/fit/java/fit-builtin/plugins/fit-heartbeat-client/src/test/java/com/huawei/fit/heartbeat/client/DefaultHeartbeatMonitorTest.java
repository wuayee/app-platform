/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.heartbeat.client;

import static com.huawei.fitframework.util.ObjectUtils.cast;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.heartbeat.HeartbeatMonitor;
import com.huawei.fit.heartbeat.HeartbeatService;
import com.huawei.fitframework.conf.runtime.WorkerConfig;
import com.huawei.fitframework.runtime.FitRuntimeStartedObserver;
import com.huawei.fitframework.schedule.ThreadPoolScheduler;
import com.huawei.fitframework.util.ReflectionUtils;
import com.huawei.fitframework.util.ThreadUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

/**
 * {@link DefaultHeartbeatMonitor} 的单元测试。
 *
 * @author 季聿阶 j00559309
 * @since 2022-09-16
 */
@DisplayName("测试 DefaultHeartbeatClient")
public class DefaultHeartbeatMonitorTest {
    private HeartbeatService heartbeatService;
    private HeartbeatMonitor monitor;

    @BeforeEach
    void setup() {
        this.heartbeatService = mock(HeartbeatService.class);
        when(this.heartbeatService.sendHeartbeat(anyList(), any())).thenReturn(true);
        when(this.heartbeatService.stopHeartbeat(anyList(), any())).thenReturn(true);
        WorkerConfig worker = mock(WorkerConfig.class);
        when(worker.id()).thenReturn("workerId");
        this.monitor = new DefaultHeartbeatMonitor(this.heartbeatService, worker);
    }

    @AfterEach
    void teardown() {
        this.monitor = null;
        this.heartbeatService = null;
    }

    @Nested
    @DisplayName("当 FIT 运行时启动之后")
    class AfterRuntimeStarted {
        @BeforeEach
        void setup() {
            FitRuntimeStartedObserver observer = cast(DefaultHeartbeatMonitorTest.this.monitor);
            observer.onRuntimeStarted(null);
        }

        @AfterEach
        void teardown() throws NoSuchFieldException, InterruptedException {
            Field field = DefaultHeartbeatMonitor.class.getDeclaredField("keepAliveScheduledExecutor");
            ThreadPoolScheduler executorService =
                    cast(ReflectionUtils.getField(DefaultHeartbeatMonitorTest.this.monitor, field));
            executorService.shutdown();
        }

        @Test
        @DisplayName("保持连接至少 2 个同步周期，成功连接心跳服务器")
        void connectServerSuccessfullyAfterKeepAliveAtLeast2Period() {
            assertThatNoException().isThrownBy(() -> DefaultHeartbeatMonitorTest.this.monitor.keepAlive("testScenario",
                    1,
                    1,
                    1));
            ThreadUtils.sleep(200);
            verify(DefaultHeartbeatMonitorTest.this.heartbeatService, atLeastOnce()).sendHeartbeat(anyList(), any());
        }

        @Test
        @DisplayName("中断连接至少 2 个同步周期，无连接到心跳服务器")
        void noConnectionToServerAfterTerminateAtLeast2Period() {
            assertThatNoException().isThrownBy(() -> DefaultHeartbeatMonitorTest.this.monitor.terminate("testScenario"
            ));
            ThreadUtils.sleep(200);
            verify(DefaultHeartbeatMonitorTest.this.heartbeatService, times(0)).sendHeartbeat(anyList(), any());
            verify(DefaultHeartbeatMonitorTest.this.heartbeatService, times(1)).stopHeartbeat(anyList(), any());
        }
    }
}
