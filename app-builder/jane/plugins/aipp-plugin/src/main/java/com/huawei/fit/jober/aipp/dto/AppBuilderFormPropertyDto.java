/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 应用构建器表单属性Dto
 *
 * @author 邬涨财 w00575064
 * @since 2024-04-19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppBuilderFormPropertyDto {
    private String id;
    private String formId;
    private String name;
    private String dataType;
    private String defaultValue;
}
