/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.po;

import com.huawei.jade.app.engine.task.entity.EvalTaskStatusEnum;
import com.huawei.jade.common.po.BasePo;

import lombok.Data;

/**
 * 评估任务 ORM 对象。
 *
 * @author 何嘉斌
 * @since 2024-08-09
 */
@Data
public class EvalTaskPo extends BasePo {
    /**
     * 评估任务名字。
     */
    private String name;

    /**
     * 评估任务描述。
     */
    private String description;

    /**
     * 评估任务状态。
     */
    private EvalTaskStatusEnum status;

    /**
     * 应用唯一标识。
     */
    private String appId;

    /**
     * 评估工作流唯一标识。
     */
    private String workflowId;
}
