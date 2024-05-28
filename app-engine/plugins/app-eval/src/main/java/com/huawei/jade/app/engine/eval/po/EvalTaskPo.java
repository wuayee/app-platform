/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评估数任务实体类。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvalTaskPo {
    private long id;
    private String appId;
    private String author;
    private double passScore;
    private LocalDateTime createTime;
    private int status = EvalTaskStatus.NOT_START.getCode();
    private String evalAlgorithmId;
    private String startNodeId;
    private String endNodeId;
    private String version;
    private double passRate = 0d;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
}
