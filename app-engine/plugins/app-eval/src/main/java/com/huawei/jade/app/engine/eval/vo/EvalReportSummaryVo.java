/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 评估报告摘要前端展示类。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvalReportSummaryVo {
    private int passNum;
    private int failureNum;
    private String algorithm;
    private double passScore;
    private List<EvalReportVo> passInput = new ArrayList<>();
    private List<EvalReportVo> failureInput = new ArrayList<>();
}
