/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fitframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 为 {@link DeleteSql} 提供基于 Postgresql 的实现。
 *
 * @author 梁济时 l00815032
 * @since 2023-10-28
 */
class PostgresDeleteSql implements DeleteSql {
    private String table;

    private Condition condition;

    @Override
    public DeleteSql from(String table) {
        this.table = table;
        return this;
    }

    @Override
    public DeleteSql where(Condition condition) {
        this.condition = condition;
        return this;
    }

    @Override
    public int execute(DynamicSqlExecutor executor) {
        notNull(executor, "The executor to execute delete sql cannot be null.");
        String actualTable = StringUtils.trim(table);
        if (StringUtils.isEmpty(actualTable)) {
            throw new IllegalStateException("The table to delete rows cannot be empty.");
        }
        if (this.condition == null) {
            throw new IllegalStateException("Cannot delete all rows in table with no condition.");
        }
        SqlBuilder sql = SqlBuilder.custom();
        sql.append("DELETE FROM ").appendIdentifier(this.table).append(" WHERE ");
        List<Object> args = new LinkedList<>();
        this.condition.toSql(sql, args);
        return executor.executeUpdate(sql.toString(), args);
    }
}
