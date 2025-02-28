/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util.sql.condition;

import modelengine.fit.jober.taskcenter.util.sql.ColumnRef;
import modelengine.fit.jober.taskcenter.util.sql.Condition;
import modelengine.fit.jober.taskcenter.util.sql.SqlBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * 表示判断指定列的值在指定区间的条件。（区间由最小值与最大值限定）
 *
 * @author 梁济时
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
