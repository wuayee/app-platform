/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import com.huawei.jade.carver.tool.model.transfer.ToolData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 表示ToolData列表和总数的Dto
 *
 * @author 姚江 yWX1299574
 * @since 2024-06-28
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToolDto {
    private List<ToolData> toolData;
    private int total;
}
