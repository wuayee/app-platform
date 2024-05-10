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
 * @since 2024-04-17
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderAppPO {
    private String id;
    private String name;
    private String tenantId;
    private String configId;
    private String flowGraphId;
    private String type;
    private String createBy;
    private String updateBy;
    private String version;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private String attributes;
    private String state;
}
