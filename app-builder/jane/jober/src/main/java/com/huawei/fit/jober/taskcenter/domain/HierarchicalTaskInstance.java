/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain;

import com.huawei.fit.jober.taskcenter.domain.util.support.HierarchicalTaskInstanceDecorator;

import java.util.List;

/**
 * 为 {@link TaskInstance} 提供层次化结构。
 *
 * @author 梁济时 l00815032
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
