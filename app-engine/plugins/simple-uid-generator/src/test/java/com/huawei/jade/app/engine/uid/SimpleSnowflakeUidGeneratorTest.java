/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.uid;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fitframework.test.annotation.FitTestWithJunit;
import com.huawei.fitframework.test.annotation.Mock;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.ReflectionUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * 表示 {@link SimpleSnowflakeUidGenerator} 的测试用例。
 *
 * @author 何嘉斌
 * @since 2024-07-29
 */
@FitTestWithJunit(includeClasses = SimpleSnowflakeUidGenerator.class)
public class SimpleSnowflakeUidGeneratorTest {
    private static final long MAX_INT = 1L << 32;

    @Mock
    private WorkerGenerator workerGenerator;

    @BeforeEach
    void setUp() {
        clearInvocations(this.workerGenerator);
    }

    @Test
    @DisplayName("获取UID时，获取成功")
    void shouldOkWhenGetUid() {
        when(this.workerGenerator.getWorkerId()).thenReturn(0);
        SimpleSnowflakeUidGenerator uidGenerator = new SimpleSnowflakeUidGenerator(this.workerGenerator);
        assertThat(uidGenerator.getUid()).isEqualTo(0);
    }

    @Test
    @DisplayName("连续获取UID时，获取UID成功")
    void shouldOkWhenGetUidWithIncreasingWorkerId() {
        when(this.workerGenerator.getWorkerId()).thenReturn(0, 1, 2);
        for (int i = 0; i < 3; i++) {
            SimpleSnowflakeUidGenerator uidGenerator = new SimpleSnowflakeUidGenerator(this.workerGenerator);
            assertThat(uidGenerator.getUid()).isEqualTo(i * MAX_INT);
        }
    }

    @Test
    @DisplayName("连续构建新UID生成器是，获取UID成功")
    void shouldOkWhenGetUidWhenUpdateWorkerId() throws NoSuchFieldException {
        when(this.workerGenerator.getWorkerId()).thenReturn(0, 1, 2, 3);
        SimpleSnowflakeUidGenerator uidGenerator = new SimpleSnowflakeUidGenerator(this.workerGenerator);
        for (int i = 1; i < 4; i++) {
            Field atomicLong = SimpleSnowflakeUidGenerator.class.getDeclaredField("id");
            AtomicLong actual = ObjectUtils.cast(ReflectionUtils.getField(uidGenerator, atomicLong));
            actual.set(1L << 34);
            assertThat(uidGenerator.getUid()).isEqualTo(i * MAX_INT);
        }
    }

    @Test
    @DisplayName("更换机器ID后，获取UID成功")
    void shouldOkWhenGetUidWhenMaxUid() throws NoSuchFieldException {
        when(this.workerGenerator.getWorkerId()).thenReturn(0, 1, 2, 3);
        SimpleSnowflakeUidGenerator uidGenerator = new SimpleSnowflakeUidGenerator(this.workerGenerator);
        Field atomicLong = SimpleSnowflakeUidGenerator.class.getDeclaredField("id");
        AtomicLong actual = ObjectUtils.cast(ReflectionUtils.getField(uidGenerator, atomicLong));
        actual.set(MAX_INT - 1L);
        assertThat(uidGenerator.getUid()).isEqualTo(MAX_INT - 1L);
        assertThat(uidGenerator.getUid()).isEqualTo(MAX_INT);
        assertThat(uidGenerator.getUid()).isEqualTo(MAX_INT + 1L);
        verify(this.workerGenerator, times(2)).getWorkerId();
    }

    @Test
    @DisplayName("更换机器ID后，获取UID成功")
    void shouldOkWhenConcurrentGetUid() throws Throwable {
        int threadNum = 8;
        long startLong = MAX_INT - 2L;

        when(this.workerGenerator.getWorkerId()).thenReturn(0, 1);
        SimpleSnowflakeUidGenerator uidGenerator = new SimpleSnowflakeUidGenerator(this.workerGenerator);
        Field atomicLong = SimpleSnowflakeUidGenerator.class.getDeclaredField("id");
        AtomicLong actual = ObjectUtils.cast(ReflectionUtils.getField(uidGenerator, atomicLong));
        actual.set(startLong);

        CountDownLatch latch = new CountDownLatch(threadNum);
        List<Long> ids = Arrays.asList(new Long[threadNum]);
        ExecutorService service =
                new ThreadPoolExecutor(0, threadNum, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
        for (int i = 0; i < threadNum; i++) {
            int index = i;
            Runnable runnable = () -> {
                try {
                    ids.set(index, uidGenerator.getUid());
                } finally {
                    latch.countDown();
                }
            };
            service.execute(runnable);
        }
        latch.await();

        assertThat(new HashSet<>(ids)).isEqualTo(LongStream.range(startLong, startLong + threadNum)
                .boxed()
                .collect(Collectors.toSet()));
    }
}
