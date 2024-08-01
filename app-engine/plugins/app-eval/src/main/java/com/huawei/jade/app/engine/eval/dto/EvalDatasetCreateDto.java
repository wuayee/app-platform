/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.dto;

import com.huawei.fitframework.annotation.Property;
import com.huawei.fitframework.validation.constraints.NotBlank;
import com.huawei.fitframework.validation.constraints.NotEmpty;

import lombok.Data;

import java.util.List;

/**
 * 表示评估数据集创建传输对象。
 *
 * @author 何嘉斌
 * @see com.huawei.jade.app.engine.eval.controller.EvalDatasetController#createEvalDataset。
 * @since 2024-07-31
 */
@Data
public class EvalDatasetCreateDto {
    @Property(description = "数据集名字", required = true)
    @NotBlank(message = "Dataset name cannot be empty.")
    private String name;

    @Property(description = "数据集介绍", required = true)
    @NotBlank(message = "Dataset description cannot be empty.")
    private String description;

    @Property(description = "评估内容", required = true)
    @NotEmpty(message = "Contents cannot be empty.")
    private List<String> contents;

    @Property(description = "数据集数据规范", required = true)
    @NotBlank(message = "Dataset schema cannot be empty.")
    private String schema;

    @Property(description = "应用编号", required = true)
    @NotBlank(message = "The application id cannot be empty.")
    private String appId;
}
