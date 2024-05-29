/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.metrics.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * MetricAccessPO类消息处理策略
 *
 * @author c00819987
 * @since 2024/05/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricsAccessPo {
    private Long id;
    private String appId;
    private Integer totalAccess;
    private LocalDateTime createTime;
}
