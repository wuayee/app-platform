/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util.support;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jober.taskcenter.domain.HierarchicalTaskInstance;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.TaskType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 为 {@link HierarchicalTaskInstance} 提供基于 {@link TaskInstance} 的适配器。
 *
 * @author 梁济时 l00815032
 * @since 2024-01-11
 */
public class HierarchicalTaskInstanceDecorator implements HierarchicalTaskInstance {
    private final TaskInstance instance;

    private final List<HierarchicalTaskInstance> children;

    public HierarchicalTaskInstanceDecorator(TaskInstance instance, List<HierarchicalTaskInstance> children) {
        this.instance = instance;
        this.children = nullIf(children, Collections.emptyList());
    }

    @Override
    public List<HierarchicalTaskInstance> children() {
        return this.children;
    }

    @Override
    public String id() {
        return this.instance.id();
    }

    @Override
    public TaskEntity task() {
        return this.instance.task();
    }

    @Override
    public TaskType type() {
        return this.instance.type();
    }

    @Override
    public SourceEntity source() {
        return this.instance.source();
    }

    @Override
    public Map<String, Object> info() {
        return this.instance.info();
    }

    @Override
    public List<String> tags() {
        return this.instance.tags();
    }

    @Override
    public List<String> categories() {
        return this.instance.categories();
    }

    @Override
    public Map<String, Object> diff(TaskInstance another) {
        return this.instance.diff(another);
    }
}
