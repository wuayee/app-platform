/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-16
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderComponentPO {
    private String id;
    private String name;
    private String type;
    private String description;
    private String formId;
    private String serviceId;
    private String tenantId;
    private String createBy;
    private String updateBy;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
