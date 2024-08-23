/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.entity;

import com.huawei.fitframework.annotation.Property;

import lombok.Data;

/**
 * 表示评估任务单用例执行结果传输对象。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@Data
public class EvalRecordEntity {
    @Property(description = "算法输入", required = true)
    private String input;

    @Property(description = "节点唯一标识", required = true)
    private String nodeId;

    @Property(description = "算法评分", required = true)
    private Double score;

    @Property(description = "评估任务用例唯一标识", required = true)
    private Long taskCaseId;
}