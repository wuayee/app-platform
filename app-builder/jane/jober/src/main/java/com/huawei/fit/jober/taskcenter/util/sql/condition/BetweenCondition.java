/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql.condition;

import com.huawei.fit.jober.taskcenter.util.sql.ColumnRef;
import com.huawei.fit.jober.taskcenter.util.sql.Condition;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * 表示判断指定列的值在指定区间的条件。（区间由最小值与最大值限定）
 *
 * @author 梁济时 l00815032
 * @since 2024-01-12
 */
public class BetweenCondition implements Condition {
    private final ColumnRef column;

    private final Object minimum;

    private final Object maximum;

    public BetweenCondition(ColumnRef column, Object minimum, Object maximum) {
        this.column = column;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    public void toSql(SqlBuilder sql, List<Object> args) {
        if (this.minimum == null) {
            if (this.maximum == null) {
                sql.append("1 <> 1");
            } else {
                sql.append(this.column).append(" >= ?");
                args.add(this.maximum);
            }
        } else {
            if (this.maximum == null) {
                sql.append(this.column).append(" >= ?");
                args.add(this.minimum);
            } else {
                sql.append(this.column).append(" >= ? AND ").append(this.column).append(" <= ?");
                args.addAll(Arrays.asList(this.minimum, this.maximum));
            }
        }
    }
}
