/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql;

import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;

/**
 * 表示一个删除语句。
 *
 * @author 梁济时
 * @since 2023-10-28
 */
public interface DeleteSql {
    /**
     * 设置待删除的数据所在的表。
     *
     * @param table 表示待删除的数据所在的表的名称的 {@link String}。
     * @return 表示当前的删除语句的 {@link DeleteSql}。
     */
    DeleteSql from(String table);

    /**
     * 设置删除数据的筛选条件。
     *
     * @param condition 表示用以筛选待删除数据的条件的 {@link Condition}。
     * @return 表示当前的删除语句的 {@link DeleteSql}。
     */
    DeleteSql where(Condition condition);

    /**
     * 执行删除语句。
     *
     * @param executor 表示动态语句执行器的 {@link DynamicSqlExecutor}。
     * @return 表示受影响的行数的 32 位整数。
     */
    int execute(DynamicSqlExecutor executor);

    /**
     * 返回一个删除语句。
     *
     * @return 表示新创建的删除语句的实例的 {@link DeleteSql}。
     */
    static DeleteSql custom() {
        return new PostgresDeleteSql();
    }
}
