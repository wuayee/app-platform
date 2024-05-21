/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import com.huawei.jade.store.model.transfer.ToolData;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * todo
 *
 * @author 邬涨财 w00575064
 * @since 2024-05-13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreNodeConfigResDto {
    @JsonProperty("basic")
    private List<StoreBasicNodeInfoDto> basicList;
    @JsonProperty("tool")
    private List<ToolData> toolList;
}
