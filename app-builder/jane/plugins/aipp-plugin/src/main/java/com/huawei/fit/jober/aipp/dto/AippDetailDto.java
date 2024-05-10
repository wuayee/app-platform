/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import com.huawei.fitframework.annotation.Property;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Aipp详情
 *
 * @author l00611472
 * @since 2023-12-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AippDetailDto extends AippOverviewDto {
    @Property(description = "流程视图定义")
    @JsonProperty("flow_view_data")
    private Map<String, Object> flowViewData;

    @Property(description = "aipp 描述")
    private String description;

    @Property(description = "aipp 头像")
    private String icon;
}
