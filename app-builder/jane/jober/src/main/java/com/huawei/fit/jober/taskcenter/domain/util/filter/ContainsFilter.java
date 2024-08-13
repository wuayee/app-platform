/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util.filter;

import com.huawei.fit.jober.taskcenter.domain.util.Filter;
import com.huawei.fit.jober.taskcenter.util.sql.ColumnRef;
import com.huawei.fit.jober.taskcenter.util.sql.Condition;

/**
 * 为指定文本的值包含指定文本提供过滤器。
 *
 * @author 梁济时
 * @since 2024-01-15
 */
public class ContainsFilter implements Filter {
    private final String value;

    public ContainsFilter(String value) {
        this.value = value;
    }

    @Override
    public boolean indexable() {
        return false;
    }

    @Override
    public Condition toCondition(ColumnRef column) {
        return Condition.contains(column, this.value);
    }
}
