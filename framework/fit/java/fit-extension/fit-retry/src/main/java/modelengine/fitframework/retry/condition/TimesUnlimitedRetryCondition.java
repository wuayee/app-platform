/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package modelengine.fitframework.retry.condition;

import modelengine.fitframework.retry.Condition;

/**
 * 表示无限次重试的重试条件。
 *
 * @author 季聿阶
 * @since 2022-11-20
 */
public class TimesUnlimitedRetryCondition implements Condition {
    @Override
    public boolean matches(int attemptTimes, long executionTimeMillis, Throwable cause) {
        return true;
    }
}
