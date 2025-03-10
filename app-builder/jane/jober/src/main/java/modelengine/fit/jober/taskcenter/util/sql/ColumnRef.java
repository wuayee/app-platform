/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util.sql;

import modelengine.fit.jober.taskcenter.util.sql.support.DefaultColumnRef;

/**
 * 表示数据列的引用。
 *
 * @author 梁济时
 * @since 2024-01-12
 */
public interface ColumnRef extends SqlElement {
    /**
     * 获取数据列所在的数据表的名称。
     *
     * @return 表示数据表的名称的 {@link String}。
     */
    String table();

    /**
     * 获取数据列的名称。
     *
     * @return 表示数据列的名称的 {@link String}。
     */
    String name();

    /**
     * 使用所属数据表的名称及数据列的名称创建数据列引用的新实例。
     * <p>{@code name} 的前后空白字符将被忽略。</p>
     *
     * @param name 表示数据列的名称的 {@link String}。
     * @return 表示新创建的数据列的引用的 {@link ColumnRef}。
     * @throws IllegalArgumentException {@code name} 是一个空白字符串。
     */
    static ColumnRef of(String name) {
        return of(null, name);
    }

    /**
     * 使用所属数据表的名称及数据列的名称创建数据列引用的新实例。
     * <p>{@code table} 和 {@code name} 的前后空白字符将被忽略。</p>
     *
     * @param table 表示所属数据表的名称的 {@link String}。
     * @param name 表示数据列的名称的 {@link String}。
     * @return 表示新创建的数据列的引用的 {@link ColumnRef}。
     * @throws IllegalArgumentException {@code name} 是一个空白字符串。
     */
    static ColumnRef of(String table, String name) {
        return new DefaultColumnRef(table, name);
    }
}
