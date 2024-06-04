/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.metrics.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * MetricsAnalyzeVO类消息处理策略
 *
 * @author c00819987
 * @since 2024/05/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricsAnalysisVo {
    Map<String, Object> basicMetrics;
    Map<String, Object> avgResponseRange;
    List<UserAccessVo> topUsers;
    List<Map<String, Object>> userAccessData;
}
