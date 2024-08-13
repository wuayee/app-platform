/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.schedule.cron;

import com.huawei.fitframework.inspection.Nonnull;

import java.time.ZonedDateTime;
import java.util.BitSet;
import java.util.Optional;

/**
 * 表示 CRON 表达式的字段。
 *
 * @author 季聿阶
 * @since 2023-01-03
 */
public interface CronField {
    /**
     * 获取当前字段的位图存储。
     *
     * @return 表示当前字段的位图存储的 {@link BitSet}。
     */
    BitSet getBitSet();

    /**
     * 将一个特殊值合并进当前字段。
     *
     * @param specialValue 表示待合并的特殊值的 {@link String}。
     */
    void mergeSpecialValue(String specialValue);

    /**
     * 求指定时间及之后第一个满足条件的时间。
     *
     * @param dateTime 表示指定时间的 {@link ZonedDateTime}。
     * @return 表示指定时间及之后第一个满足条件的时间的 {@link Optional}{@code <}{@link ZonedDateTime}{@code >}。
     */
    Optional<ZonedDateTime> findCurrentOrNextTime(@Nonnull ZonedDateTime dateTime);
}
