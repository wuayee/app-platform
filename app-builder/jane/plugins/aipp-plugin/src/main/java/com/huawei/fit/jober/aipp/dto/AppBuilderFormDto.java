/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderFormDto {
    private String id;
    private String name;
    private List<Map<String, Object>> appearance;
    private String type;
}
