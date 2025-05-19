/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain.util;

import modelengine.fit.jober.taskcenter.util.ExecutableSql;

/**
 * 为任务实例提供视图。
 *
 * @author 梁济时
 * @since 2024-01-27
 */
public interface TaskInstanceView {
    /**
     * 构建视图的 SQL。
     *
     * @return 表示视图的 SQL 的 {@link ExecutableSql}。
     */
    ExecutableSql sql();
}
