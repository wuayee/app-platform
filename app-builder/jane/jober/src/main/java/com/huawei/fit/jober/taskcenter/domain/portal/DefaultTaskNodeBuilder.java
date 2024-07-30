/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.portal;

import java.util.List;

/**
 * task节点构造器
 *
 * @author 陈镕希 c00572808
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
