/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.po;

import com.huawei.fit.jober.aipp.aop.LocaleField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AppBuilder表单属性结构体
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppBuilderFormPropertyPo {
    private String id;
    private String formId;
    private String name;
    private String dataType;
    @LocaleField
    private String defaultValue;
}
