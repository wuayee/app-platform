/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import com.huawei.fitframework.annotation.Property;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Aipp的表单组件信息
 *
 * @author x00576283
 * @since 2023/12/11
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AippFormComponentDto {
    @Property(description = "分组列表")
    @JsonProperty("groups")
    private List<AippComponentGroupDto> groups;

    @Property(description = "组件列表")
    @JsonProperty("items")
    private List<AippComponentItemDto> items;
}
