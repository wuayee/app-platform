/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain;

import modelengine.fit.jober.taskcenter.domain.util.support.HierarchicalTaskInstanceDecorator;

import java.util.List;

/**
 * 为 {@link TaskInstance} 提供层次化结构。
 *
 * @author 梁济时
 * @since 2024-01-11
 */
public interface HierarchicalTaskInstance extends TaskInstance {
    /**
     * 获取子任务实例的列表。
     *
     * @return 表示子任务实例的列表的 {@link List}{@code <}{link HierarchicalTaskInstance}{@code >}。
     */
    List<HierarchicalTaskInstance> children();

    /**
     * 使用任务实例的信息及子任务实例的列表创建 {@link HierarchicalTaskInstance} 的新实例。
     *
     * @param instance 表示任务实例的信息的 {@link TaskInstance}。
     * @param children 表示子任务实例的列表的 {@link List}{@code <}{@link HierarchicalTaskInstance}{@code >}。
     * @return 表示新创建的层次化任务实例的的 {@link HierarchicalTaskInstance}。
     */
    static HierarchicalTaskInstance of(TaskInstance instance, List<HierarchicalTaskInstance> children) {
        return new HierarchicalTaskInstanceDecorator(instance, children);
    }
}
