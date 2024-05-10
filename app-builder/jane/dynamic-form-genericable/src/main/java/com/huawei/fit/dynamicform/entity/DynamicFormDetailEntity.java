/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.dynamicform.entity;

import com.huawei.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynamicFormDetailEntity {
    @Property(description = "表单元信息")
    private DynamicFormEntity meta;

    @Property(description = "表单数据")
    private String data;
}
