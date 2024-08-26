/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import modelengine.fitframework.annotation.Property;

import java.util.List;

/**
 * Aipp的组件信息
 *
 * @author 夏斐
 * @since 2023/12/11
 */
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AippComponentItemDto {
    @Property(description = "类型")
    @JsonProperty("type")
    private String type;

    @Property(description = "名称")
    @JsonProperty("name")
    private String name;

    @Property(description = "图标")
    @JsonProperty("icon")
    private String icon;

    @Property(description = "描述")
    @JsonProperty("description")
    private String description;

    @Property(description = "分组")
    @JsonProperty("group")
    private List<String> group;
}
