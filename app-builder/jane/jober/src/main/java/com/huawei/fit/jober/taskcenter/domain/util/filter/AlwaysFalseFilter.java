/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util.filter;

import com.huawei.fit.jober.taskcenter.domain.util.Filter;
import com.huawei.fit.jober.taskcenter.util.sql.ColumnRef;
import com.huawei.fit.jober.taskcenter.util.sql.Condition;

/**
 * 为 {@link Filter} 提供表示恒为假的实现。
 *
 * @author 梁济时
 * @since 2024-01-15
 */
public class AlwaysFalseFilter implements Filter {
    /**
     * 表示当前类型的唯一实例。
     */
    public static final AlwaysFalseFilter INSTANCE = new AlwaysFalseFilter();

    private AlwaysFalseFilter() {
    }

    @Override
    public boolean indexable() {
        return true;
    }

    @Override
    public Condition toCondition(ColumnRef column) {
        return Condition.alwaysFalse();
    }
}
