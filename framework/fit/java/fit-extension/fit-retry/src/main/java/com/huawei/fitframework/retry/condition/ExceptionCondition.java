/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.retry.condition;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.retry.Condition;
import com.huawei.fitframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 表示捕获异常的重试条件。
 *
 * @author 邬涨财 w00575064
 * @since 2023-02-25
 */
public class ExceptionCondition implements Condition {
    private final List<Class<? extends Throwable>> capturedExceptions;

    public ExceptionCondition() {
        this(new ArrayList<>());
    }

    public ExceptionCondition(List<Class<? extends Throwable>> capturedExceptions) {
        this.capturedExceptions = Validation.notNull(capturedExceptions, "Captured exceptions can not be null");
    }

    @Override
    public boolean matches(int attemptTimes, long executionTimeMillis, Throwable cause) {
        return CollectionUtils.isEmpty(this.capturedExceptions) || this.capturedExceptions.stream()
                .filter(Objects::nonNull)
                .anyMatch(throwable -> throwable.isInstance(cause));
    }
}
