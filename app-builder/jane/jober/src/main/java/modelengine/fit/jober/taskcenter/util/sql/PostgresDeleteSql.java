/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util.sql;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.jober.taskcenter.util.DynamicSqlExecutor;

import modelengine.fitframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 为 {@link DeleteSql} 提供基于 Postgresql 的实现。
 *
 * @author 梁济时
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
