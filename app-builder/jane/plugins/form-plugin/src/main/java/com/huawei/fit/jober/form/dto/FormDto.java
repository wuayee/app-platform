/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.form.dto;

import com.huawei.fit.dynamicform.entity.DynamicFormEntity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 * 表单信息Dto, 字段与{@link DynamicFormEntity}相同, @JSONProperty注解暂无法跨包使用
 *
 * @author 熊以可
 * @since 2023-12-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormDto {
    @Property(description = "表单ID")
    @JsonProperty("id")
    private String id;

    @Property(description = "表单版本")
    @JsonProperty("version")
    private String version;

    @Property(description = "用户ID")
    @JsonProperty("tenant_id")
    private String tenantId;

    @Property(description = "表单名称")
    @JsonAlias({"formName"})  // for being compatible with current frontend
    @JsonProperty("form_name")
    private String formName;

    @Property(description = "表单创建时间")
    @JsonProperty("created_at")
    private String createTime;

    @Property(description = "表单创建用户")
    @JsonProperty("create_user")
    private String createUser;

    @Property(description = "表单更新时间")
    @JsonProperty("updated_at")
    private String updateTime;

    @Property(description = "表单更新用户")
    @JsonProperty("update_user")
    private String updateUser;

    public FormDto(DynamicFormEntity entity) {
        this.id = entity.getId();
        this.version = entity.getVersion();
        this.tenantId = entity.getTenantId();
        this.formName = entity.getFormName();
        this.createTime = entity.getCreateTime();
        this.createUser = entity.getCreateUser();
        this.updateTime = entity.getUpdateTime();
        this.updateUser = entity.getUpdateUser();
    }

    /**
     * 返回{@link DynamicFormEntity}实例
     *
     * @return {@link DynamicFormEntity}实例
     */
    public DynamicFormEntity toEntity() {
        return new DynamicFormEntity(id, version, tenantId, formName, createTime, createUser, updateTime, updateUser);
    }
}
