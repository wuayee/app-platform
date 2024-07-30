/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * This class is used to create a new application.
 * 应用创建Dto
 *
 * @author 姚江 yWX1299574
 * @since 2024-04-24
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderAppCreateDto {
    private String name;
    private String description;
    private String icon;
    private String greeting;
    @JsonProperty("app_type")
    private String appType;
    private String type;
    @JsonProperty("store_id")
    private String storeId;
}
