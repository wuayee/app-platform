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
 * 评估报告实体类。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvalReportPo {
    private long id;
    private String instanceId;
    private double score;
    private String meta;
    private long evalTaskId;
    private String input;
    private String expectedOutput;
    private String output;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
