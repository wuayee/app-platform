/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 邬涨财 w00575064
 * @since 2024-05-21
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppCreateToolDto {
    private String name;
    private String description;
    private String icon;
    private String greeting;
    @JsonProperty("app_type")
    private String appType;
    private String type;
    private String systemPrompt;
}
