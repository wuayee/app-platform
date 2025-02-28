/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain.portal;

import java.util.List;

/**
 * task节点构造器
 *
 * @author 陈镕希
 * @since 2023-10-30
 */
class DefaultTaskNodeBuilder implements TaskNode.Builder {
    private String id;

    private String name;

    private TaskNodeType type;

    private List<TaskNode> children;

    @Override
    public TaskNode.Builder id(String id) {
        this.id = id;
        return this;
    }

    @Override
    public TaskNode.Builder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public TaskNode.Builder type(TaskNodeType type) {
        this.type = type;
        return this;
    }

    @Override
    public TaskNode.Builder children(List<TaskNode> children) {
        this.children = children;
        return this;
    }

    @Override
    public TaskNode build() {
        return new DefaultTaskNode(this.id, this.name, this.type, this.children);
    }
}
