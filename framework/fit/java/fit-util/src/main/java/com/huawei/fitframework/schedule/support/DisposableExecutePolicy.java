/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.schedule.support;

import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.schedule.ExecutePolicy;

import java.time.Instant;
import java.util.Optional;

/**
 * 表示一次性的执行策略。
 *
 * @author 季聿阶
 * @since 2022-11-15
 */
public class DisposableExecutePolicy extends AbstractExecutePolicy {
    /** 表示一次性执行策略的单例的 {@link ExecutePolicy}。 */
    public static final ExecutePolicy INSTANCE = new DisposableExecutePolicy();

    private DisposableExecutePolicy() {}

    @Override
    public Optional<Instant> nextExecuteTime(@Nonnull Execution execution, @Nonnull Instant startTime) {
        this.validateExecutionStatus(execution.status());
        if (execution.status() == ExecutionStatus.SCHEDULING) {
            return Optional.of(startTime);
        }
        return Optional.empty();
    }
}
