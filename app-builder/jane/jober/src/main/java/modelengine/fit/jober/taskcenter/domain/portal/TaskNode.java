/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain.portal;

import java.util.List;

/**
 * 表示任务节点。
 *
 * @author 梁济时
 * @since 2023-09-14
 */
public interface TaskNode {
    /**
     * 获取任务节点的唯一标识。
     *
     * @return 表示任务节点唯一标识的 {@link String}。
     */
    String id();

    /**
     * 获取任务节点的名称。
     *
     * @return 表示任务节点名称的 {@link String}。
     */
    String name();

    /**
     * 获取任务节点的类型。
     *
     * @return 表示任务节点类型的 {@link TaskNodeType}。
     */
    TaskNodeType type();

    /**
     * 获取子节点的列表。
     *
     * @return 表示子节点列表的 {@link List}{@code <}{@link TaskNode}{@code >}。
     */
    List<TaskNode> children();

    /**
     * 为任务节点提供构建器。
     *
     * @author 梁济时
     * @since 2023-09-14
     */
    interface Builder {
        /**
         * 设置任务节点的唯一标识。
         *
         * @param id 表示任务节点的唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder id(String id);

        /**
         * 设置任务节点的名称。
         *
         * @param name 表示节点名称的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder name(String name);

        /**
         * 设置任务节点的类型。
         *
         * @param type 表示节点的类型的 {@link TaskNodeType}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder type(TaskNodeType type);

        /**
         * 设置子节点的列表。
         *
         * @param children 表示子节点列表的 {@link List}{@code <}{@link TaskNode}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder children(List<TaskNode> children);

        /**
         * 构建任务节点。
         *
         * @return 表示新构建的任务节点的 {@link TaskNode}。
         */
        TaskNode build();
    }

    /**
     * 返回一个构建器，用以构建任务节点的新实例。
     *
     * @return 表示用以构建任务节点的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultTaskNodeBuilder();
    }
}

