/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql.condition;

import com.huawei.fit.jober.taskcenter.util.sql.ColumnRef;
import com.huawei.fit.jober.taskcenter.util.sql.Condition;

/**
 * 为二元条件提供基类。
 *
 * @author 陈镕希 c00572808
 * @since 2023-10-30
 */
public abstract class AbstractBinaryCondition implements Condition {
    private final ColumnRef column;

    private final Object value;

    public AbstractBinaryCondition(ColumnRef column, Object value) {
        this.column = column;
        this.value = value;
    }

    protected final ColumnRef column() {
        return this.column;
    }

    protected Object value() {
        return this.value;
    }
}
