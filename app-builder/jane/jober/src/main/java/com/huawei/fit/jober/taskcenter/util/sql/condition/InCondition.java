/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql.condition;

import static modelengine.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.jober.taskcenter.util.sql.ColumnRef;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;
import modelengine.fitframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用以判定指定列是否在有效值域内的条件。
 *
 * @author 梁济时
 * @since 2023-11-21
 */
public class InCondition extends AbstractBinaryCondition {
    public InCondition(ColumnRef column, Collection<?> value) {
        super(column, value);
    }

    @Override
    public Collection<?> value() {
        return cast(super.value());
    }

    @Override
    public void toSql(SqlBuilder sql, List<Object> args) {
        if (CollectionUtils.isEmpty(this.value())) {
            sql.append("1 <> 1");
            return;
        }
        List<Object> nonNull = this.value().stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (nonNull.isEmpty()) {
            sql.append(this.column()).append(" IS NULL");
            return;
        }
        if (nonNull.size() < this.value().size()) {
            sql.append('(').append(this.column()).append(" IS NULL OR ");
            sql.append(this.column()).append(" IN (").appendRepeatedly("?, ", nonNull.size())
                    .backspace(2).append(')');
        } else if (nonNull.size() > 1) {
            sql.append(this.column()).append(" IN (").appendRepeatedly("?, ", nonNull.size())
                    .backspace(2).append(')');
        } else {
            sql.append(this.column()).append(" = ?");
        }
        args.addAll(nonNull);
    }
}
