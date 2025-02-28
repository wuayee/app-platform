/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util.sql;

/**
 * 表示数据列。
 *
 * @author 梁济时
 * @since 2023-12-08
 */
public interface Column extends SqlElement, Nameable {
    /**
     * 表示数据列所在的数据表。
     *
     * @return 表示数据表的名称的 {@link String}。
     */
    String table();

    /**
     * 创建数据列。
     *
     * @param table 表示数据表的名称的 {@link String}。
     * @param name 表示数据列的名称的 {@link String}。
     * @param alias 表示数据列的别名的 {@link String}。
     * @return 表示新建的数据列的 {@link Column}。
     */
    static Column of(String table, String name, String alias) {
        return new DefaultColumn(table, name, alias);
    }
}
