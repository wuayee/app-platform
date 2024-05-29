/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评估报告调用轨迹的实体类
 *
 * @since 2024/05/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EvalReportTracePo {
    private long id;
    private String instanceId;
    private String nodeId;
    private String input;
    private String output;
    private LocalDateTime time;
    private long latency;
}
