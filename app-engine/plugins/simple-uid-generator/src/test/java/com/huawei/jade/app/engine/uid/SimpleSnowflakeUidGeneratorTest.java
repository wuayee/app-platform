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
import com.huawei.fitframework.test.annotation.Mocked;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.ReflectionUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 表示 {@link SimpleSnowflakeUidGenerator} 的测试用例。
 *
 * @author 何嘉斌
 * @since 2024-07-29
 */
@FitTestWithJunit(classes = SimpleSnowflakeUidGenerator.class)
public class SimpleSnowflakeUidGeneratorTest {
    private static final long MAX_INT = 1L << 32;

    @Mocked
    private WorkerGenerator workerGenerator;

    @BeforeEach
    void setUp() {
        clearInvocations(this.workerGenerator);
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
}
