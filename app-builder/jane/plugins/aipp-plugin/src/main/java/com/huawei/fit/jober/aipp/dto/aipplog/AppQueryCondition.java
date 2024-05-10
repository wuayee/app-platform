/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto.aipplog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 邬涨财 w00575064
 * @since 2024-05-06
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppQueryCondition {
    private String tenantId;
    private String name;
    private String type;
}
