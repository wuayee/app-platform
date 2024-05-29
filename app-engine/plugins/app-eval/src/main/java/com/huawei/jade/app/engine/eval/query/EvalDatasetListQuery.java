/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.query;

import com.huawei.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评估数据集查询参数。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvalDatasetListQuery {
    @Property(description = "应用id")
    private String appId;

    @Property(description = "数据集名称")
    private String datasetName;

    @Property(description = "数据集描述")
    private String description;

    @Property(description = "创建者")
    private String author;

    @Property(description = "创建数据集起始时间")
    private LocalDateTime createTimeFrom;

    @Property(description = "创建数据集截止时间")
    private LocalDateTime createTimeTo;

    @Property(description = "编辑数据集起始时间")
    private LocalDateTime modifyTimeFrom;

    @Property(description = "编辑数据集截止时间")
    private LocalDateTime modifyTimeTo;

    @Property(description = "页码(1开始)", example = "1")
    private int pageIndex = 1;

    @Property(description = "每页大小", example = "10")
    private int pageSize = 10;
}
