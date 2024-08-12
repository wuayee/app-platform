/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.schedule.support;

import static com.huawei.fitframework.schedule.ExecutePolicy.ExecutionStatus.EXECUTED;
import static com.huawei.fitframework.schedule.ExecutePolicy.ExecutionStatus.SCHEDULING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fitframework.schedule.ExecutePolicy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

/**
 * 表示 {@link FixedDelayExecutePolicy} 的单元测试。
 *
 * @author 杭潇
 * @since 2022-12-05
 */
@DisplayName("测试 FixedDelayExecutePolicy 类")
class FixedDelayExecutePolicyTest {
    @DisplayName("状态值为 SCHEDULING 时，下次执行时间方法返回时间值与开始执行时间值相同")
    @Test
    void givenStatusIsSchedulingThenReturnValueIsTheSameAsStartTime() {
        long period = 500L;
        Instant startTime = Instant.now();

        ExecutePolicy executePolicy = ExecutePolicy.fixedDelay(period);
        ExecutePolicy.Execution execution = mock(ExecutePolicy.Execution.class);
        when(execution.status()).thenReturn(SCHEDULING);

        Optional<Instant> instant = executePolicy.nextExecuteTime(execution, startTime);
        assertThat(instant.isPresent()).isTrue();
        assertThat(instant.get()).isEqualTo(startTime);
    }

    @DisplayName("状态值为 EXECUTED 时，下次执行时间方法返回时间值与上次任务完成时间值有关")
    @Test
    void givenStatusIsExecuteThenReturnValueIsDependOnLastCompleteTime() {
        long period = 500L;
        Instant completeTime = Instant.now();
        ExecutePolicy.Execution execution = mock(ExecutePolicy.Execution.class);
        when(execution.status()).thenReturn(EXECUTED);
        when(execution.lastCompleteTime()).thenReturn(Optional.of(completeTime));

        ExecutePolicy executePolicy = ExecutePolicy.fixedDelay(period);
        Optional<Instant> optionalInstant = executePolicy.nextExecuteTime(execution, completeTime.minusSeconds(1));

        assertThat(optionalInstant.isPresent()).isTrue();
        assertThat(optionalInstant.get()).isEqualTo(Instant.ofEpochMilli(completeTime.toEpochMilli() + period));
    }
}
