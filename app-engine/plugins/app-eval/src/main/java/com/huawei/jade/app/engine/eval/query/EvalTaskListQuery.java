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
import java.util.List;

/**
 * 评估任务查询参数。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvalTaskListQuery {
    @Property(description = "应用id")
    private String appId;

    @Property(description = "应用版本号")
    private String version;

    @Property(description = "任务创建者")
    private String author;

    @Property(description = "创建任务的起始时间")
    private LocalDateTime createTimeFrom;

    @Property(description = "创建任务的截止时间")
    private LocalDateTime createTimeTo;

    @Property(description = "任务状态筛选列表")
    private List<Integer> statusList;

    @Property(description = "页码(1开始)", example = "1")
    private int pageIndex = 1;

    @Property(description = "每页大小", example = "10")
    private int pageSize = 10;
}
