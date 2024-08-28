/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.dto;

import com.huawei.jade.common.query.PageQueryParam;

import lombok.Data;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.validation.constraints.Min;

/**
 * 表示评估任务实例的查询参数。
 *
 * @author 兰宇晨
 * @see com.huawei.jade.app.engine.task.controller.EvalInstanceController#queryEvalInstance
 * @since 2024-08-15
 */
@Data
public class EvalInstanceQueryParam extends PageQueryParam {
    @Property(description = "评估任务编号", required = true)
    @Min(min = 1, message = "The taskId is not greater than 1.")
    private Long taskId;
}
