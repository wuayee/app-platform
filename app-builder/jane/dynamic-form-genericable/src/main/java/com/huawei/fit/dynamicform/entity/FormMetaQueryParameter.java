/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.fit.dynamicform.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表单元数据查询
 *
 * @author x00576283
 * @since 2023/12/13
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FormMetaQueryParameter {
    /**
     * 表单id
     * 必填
     */
    private String formId;

    /**
     * 表单版本
     * 必填
     */
    private String version;
}
