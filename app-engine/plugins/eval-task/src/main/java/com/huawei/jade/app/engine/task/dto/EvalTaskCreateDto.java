/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.dto;

import com.huawei.fitframework.annotation.Property;
import com.huawei.fitframework.validation.constraints.NotBlank;
import com.huawei.fitframework.validation.constraints.NotEmpty;

import lombok.Data;

/**
 * 表示评估任务创建传输对象。
 *
 * @author 何嘉斌
 * @see com.huawei.jade.app.engine.task.controller.EvalTaskController#createEvalTask。
 * @since 2024-08-09
 */
@Data
public class EvalTaskCreateDto {
    @Property(description = "评估任务名字", required = true)
    @NotBlank(message = "Task name cannot be empty.")
    private String name;

    @Property(description = "评估任务介绍", required = true)
    @NotBlank(message = "Task description cannot be empty.")
    private String description;

    @Property(description = "状态", required = true)
    @NotEmpty(message = "Status cannot be empty.")
    private String status;

    @Property(description = "应用编号", required = true)
    @NotBlank(message = "The application id cannot be empty.")
    private String appId;

    @Property(description = "评估工作流编号", required = true)
    @NotBlank(message = "The workflow id cannot be empty.")
    private String workflowId;
}
