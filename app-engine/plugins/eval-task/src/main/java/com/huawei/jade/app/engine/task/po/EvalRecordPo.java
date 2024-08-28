/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.po;

import com.huawei.jade.common.po.BasePo;

import lombok.Data;

/**
 * 评估任务用例结果 ORM 对象。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@Data
public class EvalRecordPo extends BasePo {
    /**
     * 算法输入。
     */
    private String input;

    /**
     * 节点唯一标识。
     */
    private String nodeId;

    /**
     * 算法评分。
     */
    private Double score;

    /**
     * 评估任务用例唯一标识。
     */
    private Long taskCaseId;
}