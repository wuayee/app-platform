/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表示 config 表单项 的 dto 对象。
 *
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderConfigFormPropertyDto {
    private String id;
    private String name;
    private String dataType;
    private Object defaultValue;
    private String nodeId;
}
