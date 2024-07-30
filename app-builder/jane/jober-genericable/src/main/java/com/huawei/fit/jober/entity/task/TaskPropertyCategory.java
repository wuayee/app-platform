/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.entity.task;

import java.util.Objects;

/**
 * 表示任务属性类别。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-29
 */
public class TaskPropertyCategory {
    private String value;

    private String category;

    /**
     * TaskPropertyCategory
     */
    public TaskPropertyCategory() {
    }

    public TaskPropertyCategory(String value, String category) {
        this.value = value;
        this.category = category;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TaskPropertyCategory that = (TaskPropertyCategory) o;
        return Objects.equals(value, that.value) && Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, category);
    }

    @Override
    public String toString() {
        return "TaskPropertyCategory{" + "value='" + value + '\'' + ", category='" + category + '\'' + '}';
    }
}
