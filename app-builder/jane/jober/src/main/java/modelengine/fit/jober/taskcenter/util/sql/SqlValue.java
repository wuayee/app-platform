/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util.sql;

/**
 * 表示 SQL 的数据。
 *
 * @author 梁济时
 * @since 2023-10-24
 */
public interface SqlValue {
    /**
     * 包装占位符。
     *
     * @param placeholder 表示待包装的占位符的 {@link String}。
     * @return 表示包装后的占位符的 {@link String}。
     */
    String wrapPlaceholder(String placeholder);

    /**
     * 获取值。
     *
     * @return 表示值的 {@link Object}。
     */
    Object get();

    /**
     * 创建一个 JSON 的值。
     *
     * @param value 表示 JSON 值的 {@link Object}。
     * @return 表示 JSON 值的 {@link SqlValue}。
     */
    static SqlValue json(Object value) {
        return new SqlValues.JsonSqlValue(value);
    }
}
