/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.entity.task;

import java.util.List;
import java.util.Map;

/**
 * 表示任务属性。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-29
 */
public class TaskProperty {
    private String id;

    private String name;

    private String dataType;

    private int sequence;

    private String description;

    private boolean required;

    private boolean identifiable;

    private String scope;

    private Map<String, Object> appearance;

    private List<TaskPropertyCategory> categories;

    public TaskProperty() {
    }

    public TaskProperty(String id, String name, String dataType, int sequence, String description, boolean required,
            boolean identifiable, String scope, Map<String, Object> appearance, List<TaskPropertyCategory> categories) {
        this.id = id;
        this.name = name;
        this.dataType = dataType;
        this.sequence = sequence;
        this.description = description;
        this.required = required;
        this.identifiable = identifiable;
        this.scope = scope;
        this.appearance = appearance;
        this.categories = categories;
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

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isIdentifiable() {
        return identifiable;
    }

    public void setIdentifiable(boolean identifiable) {
        this.identifiable = identifiable;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Map<String, Object> getAppearance() {
        return appearance;
    }

    public void setAppearance(Map<String, Object> appearance) {
        this.appearance = appearance;
    }

    public List<TaskPropertyCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<TaskPropertyCategory> categories) {
        this.categories = categories;
    }
}
