/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.dto;

import com.huawei.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 创建评估任务所需参数。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvalTaskDto {
    @Property(description = "被评测应用的id")
    private String appId;

    @Property(description = "任务创建者")
    private String author;

    @Property(description = "本次任务涉及到数据集id列表")
    private List<Long> datasetIds;

    @Property(description = "评估算法的id")
    private String evalAlgorithmId;

    @Property(description = "设置评测应用的起始节点")
    private String startNodeId;

    @Property(description = "设置评测应用的结束节点")
    private String endNodeId;

    @Property(description = "及格分")
    private double passScore;

    @Property(description = "应用版本")
    private String version;
}
