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

/**
 * 组件分组信息
 *
 * @author 夏斐
 * @since 2023/12/11
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AippComponentGroupDto {
    @Property(description = "分组类别")
    @JsonProperty("type")
    private String type;

    @Property(description = "分组名称")
    @JsonProperty("name")
    private String name;
}
