/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.retry;

/**
 * 表示任务执行的条件。
 *
 * @author 季聿阶 j00559309
 * @since 2022-11-17
 */
public interface Condition {
    /**
     * 当业务抛出异常时，判断是否满足任务执行的条件。
     *
     * @param attemptTimes 表示已经尝试的次数的 {@code int}。
     * @param executionTimeMillis 表示当前执行业务逻辑的时间的 {@code long}。
     * @param cause 表示当前执行业务逻辑抛出的异常的 {@link Throwable}。
     * @return 表示是否满足任务执行条件的 {@code boolean}。
     */
    boolean matches(int attemptTimes, long executionTimeMillis, Throwable cause);
}
