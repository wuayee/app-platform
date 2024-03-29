/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.retry;

/**
 * 表示重试的异常。
 *
 * @author 季聿阶 j00559309
 * @since 2022-11-17
 */
public class RetryException extends RuntimeException {
    private final int attemptTimes;

    public RetryException(int attemptTimes, Throwable cause) {
        super(cause);
        this.attemptTimes = attemptTimes;
    }

    public int getAttemptTimes() {
        return this.attemptTimes;
    }
}
