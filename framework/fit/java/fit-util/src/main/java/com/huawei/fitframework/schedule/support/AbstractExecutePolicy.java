/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.schedule.support;

import static com.huawei.fitframework.inspection.Validation.isTrue;

import com.huawei.fitframework.schedule.ExecutePolicy;

/**
 * 表示 {@link ExecutePolicy} 的抽象父类。
 *
 * @author 季聿阶 j00559309
 * @since 2022-11-16
 */
public abstract class AbstractExecutePolicy implements ExecutePolicy {
    /**
     * 校验执行状态。
     *
     * @param status 表示待校验的执行状态的 {@link ExecutionStatus}。
     * @throws IllegalArgumentException 当 {@code status} 不为 {@link ExecutionStatus#SCHEDULING} 或 {@link
     * ExecutionStatus#EXECUTED} 时。
     */
    protected void validateExecutionStatus(ExecutionStatus status) {
        isTrue(status == ExecutionStatus.SCHEDULING || status == ExecutionStatus.EXECUTED,
                "The execution status must be SCHEDULING or EXECUTED. [status={0}]",
                status);
    }
}
