/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql.condition;

import com.huawei.fit.jober.taskcenter.util.sql.ColumnRef;

/**
 * 表示检查指定文本列包含指定文本值的条件。
 *
 * @author 梁济时
 * @since 2024-01-12
 */
public class ContainsCondition extends AbstractLikeCondition {
    public ContainsCondition(ColumnRef column, String value) {
        super(column, value);
    }

    @Override
    protected String wrapValue(String value) {
        return "%" + value + "%";
    }
}
