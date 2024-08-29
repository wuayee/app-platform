/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.po;

import com.huawei.jade.common.po.BasePo;

import lombok.Data;

/**
 * 评估任务报告 ORM 对象。
 *
 * @author 何嘉斌
 * @since 2024-08-14
 */
@Data
public class EvalReportPo extends BasePo {
    /**
     * 评估节点命名。
     */
    private String nodeName;

    /**
     * 评估算法及格分。
     */
    private Double passScore;

    /**
     * 评估算法格式规范。
     */
    private String algorithmSchema;

    /**
     * 评估算法平均分。
     */
    private Double averageScore;

    /**
     * 评估算法直方图。
     */
    private String histogram;

    /**
     * 评估算法实例编号。
     */
    private Long instanceId;
}