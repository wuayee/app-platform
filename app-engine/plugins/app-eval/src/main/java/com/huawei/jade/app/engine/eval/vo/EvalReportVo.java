/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 评估报告前端展示类。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvalReportVo {
    private long id;
    private String input;
    private String output;
    private String expectedOutput;
    private long latency; // 单位ms
    private double score;
    private String meta;
    private List<EvalReportTraceVo> trace;
}
