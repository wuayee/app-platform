/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 姚江 yWX1299574
 * @since 2024-06-28
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WaterflowDto {
    private List<AppBuilderWaterFlowInfoDto> waterFlowInfos;
    private int total;
}
