/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util.sql;

import modelengine.fit.jober.taskcenter.util.DynamicSqlExecutor;

/**
 * 表示更新语句。
 *
 * @author 梁济时
 * @since 2023-10-24
 */
public interface UpdateSql {
    /**
     * 设置待更新的表的名称。
     *
     * @param tableName 表示表名的 {@link String}。
     * @return 表示当前 SQL 的 {@link UpdateSql}。
     */
    UpdateSql table(String tableName);

    /**
     * 设置字段的值。
     *
     * @param column 表示待设置值的数据列的 {@link String}。
     * @param value 表示待设置到数据列的值的 {@link Object}。
     * @return 表示当前 SQL 的 {@link UpdateSql}。
     */
    UpdateSql set(String column, Object value);

    /**
     * 设置过滤条件。
     *
     * @param condition 表示过滤条件的 {@link Condition}。
     * @return 表示当前 SQL 的 {@link UpdateSql}。
     */
    UpdateSql where(Condition condition);

    /**
     * 执行 SQL。
     *
     * @param executor 表示 SQL 的执行器的 {@link DynamicSqlExecutor}。
     * @return 表示受影响的行数的 32 位整数。
     */
    int execute(DynamicSqlExecutor executor);

    /**
     * 返回一个用以更新的 SQL 的新实例。
     *
     * @return 表示用以更新的 SQL 的新实例的 {@link UpdateSql}。
     */
    static UpdateSql custom() {
        return new PostgresUpdateSql();
    }
}
