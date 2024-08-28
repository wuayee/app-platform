/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表示评估任务实例的实体。
 *
 * @author 兰宇晨
 * @since 2024-08-14
 */
@Data
public class EvalInstanceEntity {
    /**
     * 主键。
     */
    private long id;

    /**
     * 任务实例状态。
     */
    private EvalInstanceStatusEnum status;

    /**
     * 任务实例通过率。
     */
    private double passRate;

    /**
     * 任务实例创建人。
     */
    private String createdBy;

    /**
     * 任务实例创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 任务实例完成时间。
     */
    private String finishedAt;

    /**
     * 评估任务唯一标识。
     */
    private long taskId;
}
