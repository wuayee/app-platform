/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util.sql;

/**
 * 表示可命名的对象。
 *
 * @author 梁济时
 * @since 2023-12-11
 */
public interface Nameable {
    /**
     * 获取对象的名称。
     *
     * @return 表示对象名称的 {@link String}。
     */
    String name();

    /**
     * 获取对象的别名。
     *
     * @return 表示对象别名的 {@link String}。
     */
    String alias();
}
