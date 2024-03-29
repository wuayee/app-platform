/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.schedule.support;

import static com.huawei.fitframework.inspection.Validation.isTrue;
import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.schedule.ExecutePolicy.ExecutionStatus.EXECUTED;
import static com.huawei.fitframework.schedule.ExecutePolicy.ExecutionStatus.EXECUTING;
import static com.huawei.fitframework.schedule.ExecutePolicy.ExecutionStatus.SCHEDULED;
import static com.huawei.fitframework.schedule.ExecutePolicy.ExecutionStatus.SCHEDULING;

import com.huawei.fitframework.schedule.ExecutePolicy;
import com.huawei.fitframework.schedule.ExecutePolicy.ExecutionStatus;

import java.time.Instant;
import java.util.Optional;

/**
 * 表示 {@link com.huawei.fitframework.schedule.ExecutePolicy.Execution} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2022-11-16
 */
public class ReschedulableExecution implements ExecutePolicy.Execution {
    private volatile ExecutionStatus status = SCHEDULING;
    private volatile Instant lastScheduledTime;
    private volatile Instant lastExecuteTime;
    private volatile Instant lastCompleteTime;

    /**
     * 修改调度时间。
     *
     * @param lastScheduledTime 表示调度时间的 {@link Instant}。
     * @throws IllegalArgumentException 当当前执行状态不为 {@link ExecutionStatus#SCHEDULING} 或
     * {@link ExecutionStatus#EXECUTED} 时。
     * @throws IllegalArgumentException 当 {@code lastScheduledTime} 为 {@code null} 时。
     */
    void updateScheduledTime(Instant lastScheduledTime) {
        isTrue(this.status == SCHEDULING || this.status == EXECUTED,
                "Failed to update scheduled time: The execution status is incorrect. [status={0}]",
                this.status);
        this.lastScheduledTime = notNull(lastScheduledTime, "The last scheduled time cannot be null.");
        this.status = SCHEDULED;
    }

    /**
     * 修改开始执行时间。
     *
     * @param lastExecuteTime 表示开始执行时间的 {@link Instant}。
     * @throws IllegalArgumentException 当当前执行状态不为 {@link ExecutionStatus#SCHEDULED} 时。
     * @throws IllegalArgumentException 当 {@code lastExecuteTime} 为 {@code null} 时。
     */
    void updateExecuteTime(Instant lastExecuteTime) {
        isTrue(this.status == SCHEDULED,
                "Failed to update execute time: The execution status is incorrect. [status={0}]",
                this.status);
        this.lastExecuteTime = notNull(lastExecuteTime, "The last execute time cannot be null.");
        this.status = EXECUTING;
    }

    /**
     * 修改执行完毕时间。
     *
     * @param lastCompleteTime 表示执行完毕时间的 {@link Instant}。
     * @throws IllegalArgumentException 当当前执行状态不为 {@link ExecutionStatus#EXECUTING} 时。
     * @throws IllegalArgumentException 当 {@code lastCompleteTime} 为 {@code null} 时。
     */
    void updateCompleteTime(Instant lastCompleteTime) {
        isTrue(this.status == EXECUTING,
                "Failed to update complete time: The execution status is incorrect. [status={0}]",
                this.status);
        this.lastCompleteTime = notNull(lastCompleteTime, "The last complete time cannot be null.");
        this.status = EXECUTED;
    }

    @Override
    public ExecutionStatus status() {
        return this.status;
    }

    @Override
    public Optional<Instant> lastScheduledTime() {
        return Optional.ofNullable(this.lastScheduledTime);
    }

    @Override
    public Optional<Instant> lastExecuteTime() {
        return Optional.ofNullable(this.lastExecuteTime);
    }

    @Override
    public Optional<Instant> lastCompleteTime() {
        return Optional.ofNullable(this.lastCompleteTime);
    }
}
