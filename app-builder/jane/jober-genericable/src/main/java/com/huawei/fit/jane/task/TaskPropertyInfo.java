/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task;

import com.huawei.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * 表示任务定义中的属性信息。
 *
 * @author 梁济时 l00815032
 * @since 2023-11-07
 */
public class TaskPropertyInfo {
    private String id;

    private String name;

    private String dataType;

    private String description;

    private Boolean required;

    private Boolean identifiable;

    private String scope;

    public TaskPropertyInfo() {
        this(null, null, null, null, null, null, null);
    }

    public TaskPropertyInfo(String id, String name, String dataType, String description, Boolean required,
            Boolean identifiable, String scope) {
        this.id = id;
        this.name = name;
        this.dataType = dataType;
        this.description = description;
        this.required = required;
        this.identifiable = identifiable;
        this.scope = scope;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getIdentifiable() {
        return identifiable;
    }

    public void setIdentifiable(Boolean identifiable) {
        this.identifiable = identifiable;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            TaskPropertyInfo another = (TaskPropertyInfo) obj;
            return Objects.equals(this.getId(), another.getId()) && Objects.equals(this.getName(), another.getName())
                    && Objects.equals(this.getDataType(), another.getDataType()) && Objects.equals(
                    this.getDescription(), another.getDescription()) && Objects.equals(this.getRequired(),
                    another.getRequired()) && Objects.equals(this.getIdentifiable(), another.getIdentifiable())
                    && Objects.equals(this.getScope(), another.getScope());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                this.getClass(), this.getId(), this.getName(), this.getDataType(), this.getDescription(),
                this.getRequired(), this.getIdentifiable(), this.getScope()
        });
    }

    @Override
    public String toString() {
        return StringUtils.format(
                "[id={0}, name={1}, dataType={2}, description={3}, required={4}, identifiable={5}, scope={6}]",
                this.getId(), this.getName(), this.getDataType(), this.getDescription(), this.getRequired(),
                this.getIdentifiable(), this.getScope());
    }
}
