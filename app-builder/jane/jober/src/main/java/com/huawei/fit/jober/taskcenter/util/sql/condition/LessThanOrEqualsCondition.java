/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql.condition;

import com.huawei.fit.jober.taskcenter.util.sql.ColumnRef;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;

import java.util.List;

/**
 * 表示判定指定列的值小于或等于指定值的条件。
 *
 * @author 梁济时
 * @since 2024-01-24
 */
public class LessThanOrEqualsCondition extends AbstractBinaryCondition {
    public LessThanOrEqualsCondition(ColumnRef column, Object value) {
        super(column, value);
    }

    @Override
    public void toSql(SqlBuilder sql, List<Object> args) {
        sql.append(this.column()).append(" <= ?");
        args.add(this.value());
    }
}
