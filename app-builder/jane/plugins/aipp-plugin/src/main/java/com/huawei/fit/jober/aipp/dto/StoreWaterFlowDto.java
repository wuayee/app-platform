/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */
package com.huawei.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 根据store_id从app_builder_app获取waterFlow部分信息的格式
 *
 * @author 陈潇文 c00816135
 * @since 2024-05-15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreWaterFlowDto {

    /**
     * id appId
     */
    String id;

    /**
     * version 版本
     */
    String version;

    /**
     * tenantId 租户id
     */
    String tenantId;
}
