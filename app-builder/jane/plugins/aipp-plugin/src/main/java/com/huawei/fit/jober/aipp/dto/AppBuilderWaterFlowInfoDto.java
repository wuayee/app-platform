/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import com.huawei.jade.carver.tool.model.transfer.ToolData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AppBuilder waterFlow列表返回数据结构
 *
 * @author 陈潇文 c00816135
 * @since 2024-05-15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderWaterFlowInfoDto {
    /**
     * itemData store里waterFlow的元数据
     */
    private ToolData itemData;

    /**
     * tenantId 租户id
     */
    private String tenantId;

    /**
     * appId appId
     */
    private String appId;

    /**
     * version 版本号
     */
    private String version;
}
