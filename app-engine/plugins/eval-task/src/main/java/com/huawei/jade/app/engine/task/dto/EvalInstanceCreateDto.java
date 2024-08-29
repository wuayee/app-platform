/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.dto;

import lombok.Data;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.validation.constraints.Range;

/**
 * 表示评估任务实例创建传输对象。
 *
 * @author 何嘉斌
 * @see com.huawei.jade.app.engine.task.controller.EvalInstanceController#createEvalInstance
 * @since 2024-08-12
 */
@Data
public class EvalInstanceCreateDto {
    @Property(description = "评估任务编号", required = true, defaultValue = "1")
    @Range(min = 1, max = Long.MAX_VALUE, message = "The task id is invalid.")
    private Long taskId;
}