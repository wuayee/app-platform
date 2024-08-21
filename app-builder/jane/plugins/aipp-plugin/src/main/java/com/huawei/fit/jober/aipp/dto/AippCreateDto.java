/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import modelengine.fitframework.annotation.Property;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建Aipp响应体
 *
 * @author 刘信宏
 * @since 2023-12-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AippCreateDto {
    @Property(description = "aipp id")
    @JsonProperty("aipp_id")
    private String aippId;

    @Property(description = "aipp version")
    @JsonProperty("version")
    private String version;

    @Property(description = "tool unique name")
    @JsonProperty("tool_unique_name")
    private String toolUniqueName;
}
