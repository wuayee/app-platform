/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.vo;

import com.huawei.jade.app.engine.eval.po.EvalTaskStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 评估任务前端展示类。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvalTaskVo {
    private long id;
    private String author;
    private LocalDateTime createTime;
    private int status = EvalTaskStatus.NOT_START.getCode();
    private double passRate;
    private List<String> datasets = new ArrayList<>();
    private String version;
}
