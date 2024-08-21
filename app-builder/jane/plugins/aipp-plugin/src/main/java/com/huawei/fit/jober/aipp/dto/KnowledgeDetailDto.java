/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import modelengine.fitframework.annotation.Property;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 知识库详细信息
 *
 * @author 黄夏露
 * @since 2024-04-23
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeDetailDto {
    @Property(description = "知识库 id")
    @JsonProperty("id")
    private Long id;

    @Property(description = "知识库名称")
    @JsonProperty("name")
    private String name;

    @Property(description = "知识库描述")
    @JsonProperty("description")
    private String description;
}
