/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.po;

import com.huawei.jade.common.po.BasePo;

import lombok.Data;

/**
 * 评估任务用例 ORM 对象。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@Data
public class EvalCasePo extends BasePo {
    /**
     * 评估耗时。
     */
    private Integer latency;

    /**
     * 评估用例通过结果。
     */
    private Boolean outcome;

    /**
     * 评估任务实例唯一标识。
     */
    private Long instanceId;
}