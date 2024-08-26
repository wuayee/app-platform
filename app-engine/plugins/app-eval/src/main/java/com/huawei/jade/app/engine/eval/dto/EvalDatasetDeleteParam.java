/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.dto;

import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.validation.constraints.NotEmpty;
import com.huawei.jade.app.engine.eval.constraint.ValidList;

import lombok.Data;

import java.util.List;

/**
 * 表示评估数据集删除传输对象。
 *
 * @author 何嘉斌
 * @see com.huawei.jade.app.engine.eval.controller.EvalDatasetController#deleteById
 * @since 2024-07-31
 */
@Data
public class EvalDatasetDeleteParam {
    @Property(description = "数据集编号", required = true)
    @NotEmpty(message = "The datasetIds cannot be empty.")
    @ValidList(min = 1, max = Long.MAX_VALUE, message = "Some ids are invalid.")
    private List<Long> datasetIds;
}