/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain.portal;

import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 默认task节点
 *
 * @author 陈镕希
 * @since 2023-10-30
 */
class DefaultTaskNode implements TaskNode {
    private final String id;

    private final String name;

    private final TaskNodeType type;

    private final List<TaskNode> children;

    DefaultTaskNode(String id, String name, TaskNodeType type, List<TaskNode> children) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.children = children;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public TaskNodeType type() {
        return this.type;
    }

    @Override
    public List<TaskNode> children() {
        return this.children;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DefaultTaskNode) {
            DefaultTaskNode another = (DefaultTaskNode) obj;
            return Objects.equals(this.id, another.id) && Objects.equals(this.name, another.name)
                    && this.type == another.type && CollectionUtils.equals(this.children, another.children);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        List<Object> values = new LinkedList<>();
        values.add(this.id);
        values.add(this.name);
        values.add(this.type);
        if (this.children != null) {
            values.addAll(this.children);
        }
        return Arrays.hashCode(values.toArray());
    }

    @Override
    public String toString() {
        return StringUtils.format("[id={0}, name={1}, type={2}, children={3}]",
                this.id,
                this.name,
                this.type,
                this.children);
    }
}
