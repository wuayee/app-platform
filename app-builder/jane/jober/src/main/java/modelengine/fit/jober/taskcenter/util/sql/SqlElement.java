/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util.sql;

/**
 * 表示 SQL 中的元素。
 *
 * @author 梁济时
 * @since 2023-12-08
 */
public interface SqlElement {
    /**
     * 将元素追加到 SQL 构建器中。
     *
     * @param sql 表示待追加元素的 SQL 构建器的 {@link SqlBuilder}。
     */
    void appendTo(SqlBuilder sql);
}
