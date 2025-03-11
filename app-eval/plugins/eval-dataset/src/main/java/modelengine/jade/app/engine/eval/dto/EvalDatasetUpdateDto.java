/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.jade.app.engine.eval.dto;

import lombok.Data;
import modelengine.jade.app.engine.eval.controller.EvalDatasetController;
import modelengine.fitframework.annotation.Property;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * 表示评估数据集修改传输对象。
 *
 * @author 兰宇晨
 * @see EvalDatasetController#updateEvalDataset 。
 * @since 2024-08-02
 */
@Data
public class EvalDatasetUpdateDto {
    @Property(description = "数据集唯一标识", required = true)
    @NotNull(message = "The dataset id cannot be null.")
    @Positive(message = "The dataset id is invalid.")
    private Long id;

    @Property(description = "数据集名字", required = true)
    private String name;

    @Property(description = "数据集介绍", required = true)
    private String description;
}