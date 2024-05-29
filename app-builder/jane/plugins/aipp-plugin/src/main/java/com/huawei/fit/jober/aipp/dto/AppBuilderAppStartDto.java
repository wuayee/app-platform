/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 启动Aipp实例响应体
 *
 * @author 陈潇文 c00816135
 * @since 2024-05-24
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderAppStartDto {
    /**
     * aippCreateDto 创建Aipp响应体
     */
    private AippCreateDto aippCreateDto;

    /**
     * instanceId 实例Id
     */
    private String instanceId;
}
