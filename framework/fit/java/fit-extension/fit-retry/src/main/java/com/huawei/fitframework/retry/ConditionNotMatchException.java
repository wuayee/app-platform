/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.retry;

/**
 * 表示重试条件不满足的异常。
 *
 * @author 季聿阶
 * @since 2022-11-17
 */
public class ConditionNotMatchException extends RetryException {
    public ConditionNotMatchException(int attemptTimes, Throwable cause) {
        super(attemptTimes, cause);
    }
}
