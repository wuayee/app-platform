/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 表示任务定义中的类型信息。
 *
 * @author 梁济时
 * @since 2023-11-07
 */
public class TaskTypeInfo {
    private String id;

    private String name;

    private List<TaskTypeInfo> children;

    public TaskTypeInfo() {
        this(null, null, null);
    }

    public TaskTypeInfo(String id, String name, List<TaskTypeInfo> children) {
        this.id = id;
        this.name = name;
        this.children = children;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TaskTypeInfo> getChildren() {
        return children;
    }

    public void setChildren(List<TaskTypeInfo> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            TaskTypeInfo another = (TaskTypeInfo) obj;
            return Objects.equals(this.getId(), another.getId()) && Objects.equals(this.getName(), another.getName())
                    && CollectionUtils.equals(this.getChildren(), another.getChildren());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        List<TaskTypeInfo> actualChildren = nullIf(this.getChildren(), Collections.emptyList());
        List<Object> objects = new ArrayList<>(actualChildren.size() + 3);
        objects.add(this.getClass());
        objects.add(this.getId());
        objects.add(this.getName());
        objects.addAll(actualChildren);
        return Arrays.hashCode(objects.toArray());
    }

    @Override
    public String toString() {
        return StringUtils.format("[id={0}, name={1}, children={2}]", this.getId(), this.getName(),
                CollectionUtils.toString(this.getChildren()));
    }
}
