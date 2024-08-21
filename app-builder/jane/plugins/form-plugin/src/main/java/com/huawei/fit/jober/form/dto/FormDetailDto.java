/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.form.dto;

import com.huawei.fit.dynamicform.entity.DynamicFormDetailEntity;
import modelengine.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表单信息Dto, 字段与{@link DynamicFormDetailEntity}相同, @JSONProperty注解暂无法跨包使用
 *
 * @author 熊以可
 * @since 2023-12-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormDetailDto {
    @Property(description = "表单元信息")
    FormDto meta;

    @Property(description = "表单数据")
    private String data;

    public FormDetailDto(DynamicFormDetailEntity detailEntity) {
        this.meta = new FormDto(detailEntity.getMeta());
        this.data = detailEntity.getData();
    }

    /**
     * 返回{@link DynamicFormDetailEntity}实例
     *
     * @return {@link DynamicFormDetailEntity}实例
     */
    public DynamicFormDetailEntity toEntity() {
        return new DynamicFormDetailEntity(this.meta.toEntity(), this.data);
    }
}
