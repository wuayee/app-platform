/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.dto;

import com.huawei.fitframework.annotation.Property;
import com.huawei.fitframework.validation.constraints.Range;

import lombok.Data;

/**
 * 表示评估数据集修改传输对象。
 *
 * @author 兰宇晨
 * @see com.huawei.jade.app.engine.eval.controller.EvalDatasetController#updateEvalDataset 。
 * @since 2024-08-02
 */
@Data
public class EvalDatasetUpdateDto {
    @Property(description = "数据集唯一标识", required = true)
    @Range(min = 1, max = Long.MAX_VALUE, message = "Dataset ID range should between [1, Long.MAX_VALUE].")
    private Long id;

    @Property(description = "数据集名字", required = true)
    private String name;

    @Property(description = "数据集介绍", required = true)
    private String description;
}
