/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.retry.condition;

import com.huawei.fitframework.retry.Condition;

/**
 * 表示无限次重试的重试条件。
 *
 * @author 季聿阶 j00559309
 * @since 2022-11-20
 */
public class TimesUnlimitedRetryCondition implements Condition {
    @Override
    public boolean matches(int attemptTimes, long executionTimeMillis, Throwable cause) {
        return true;
    }
}
