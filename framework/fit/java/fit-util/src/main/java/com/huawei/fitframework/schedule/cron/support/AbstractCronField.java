/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.schedule.cron.support;

import com.huawei.fitframework.schedule.cron.CronField;

import java.util.BitSet;

/**
 * 表示 {@link CronField} 的抽象实现类。
 *
 * @author 季聿阶
 * @since 2023-01-03
 */
public abstract class AbstractCronField implements CronField {
    private final BitSet bitSet = new BitSet(64);

    @Override
    public BitSet getBitSet() {
        return this.bitSet;
    }

    @Override
    public void mergeSpecialValue(String specialValue) {}
}
