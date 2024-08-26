/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.dynamicform.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 * 表单详情实体类
 *
 * @author 李鑫
 * @since 2024/7/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynamicFormDetailEntity {
    @Property(description = "表单元信息")
    private DynamicFormEntity meta;

    @Property(description = "表单数据")
    private String data;
}
