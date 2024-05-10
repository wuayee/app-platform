/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import com.huawei.fitframework.annotation.Property;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Aipp列表一览返回结构
 *
 * @author x00649642
 * @since 2024-03-01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AippOverviewRspDto extends AippOverviewDto {
    @Property(description = "aipp 草稿版本", example = "1.0.1")
    @JsonProperty("draft_version")
    private String draftVersion;
}
