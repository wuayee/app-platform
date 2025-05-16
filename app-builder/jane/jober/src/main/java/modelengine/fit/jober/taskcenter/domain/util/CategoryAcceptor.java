/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain.util;

import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.domain.util.support.DefaultCategoryAcceptor;

import java.util.List;
import java.util.Map;

/**
 * 为类目提供配置器。
 *
 * @author 梁济时
 * @since 2023-08-25
 */
public interface CategoryAcceptor {
    /**
     * 根据任务数据获取类目。
     *
     * @param info 表示任务数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示类目的列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> obtain(Map<String, Object> info);

    /**
     * of
     *
     * @param task task
     * @return CategoryAcceptor
     */
    static CategoryAcceptor of(TaskEntity task) {
        return new DefaultCategoryAcceptor(task.getProperties());
    }
}
