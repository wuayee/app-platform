/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.dto;

import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.validation.constraints.NotEmpty;
import modelengine.fitframework.validation.constraints.Range;

import lombok.Data;

import java.util.List;

/**
 * 表示评估数据集创建传输对象。
 *
 * @author 易文渊
 * @see com.huawei.jade.app.engine.eval.controller.EvalDataController#createEvalData。
 * @since 2024-07-19
 */
@Data
public class EvalDataCreateDto {
    @Property(description = "数据集编号", required = true, defaultValue = "1")
    @Range(min = 1, max = Long.MAX_VALUE, message = "The dataset id is invalid.")
    private Long datasetId;

    @Property(description = "评估内容列表", required = true)
    @NotEmpty(message = "The contents cannot be empty.")
    private List<String> contents;
}