/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.dto;

import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.validation.constraints.NotBlank;
import com.huawei.jade.common.query.PageQueryParam;

import lombok.Data;

/**
 * 表示数据集查询参数。
 *
 * @author 兰宇晨
 * @see com.huawei.jade.app.engine.eval.controller.EvalDatasetController#queryEvalDataset
 * @since 2024-07-24
 */
@Data
public class EvalDatasetQueryParam extends PageQueryParam {
    @Property(description = "应用 ID", required = true)
    @NotBlank(message = "appId should not be null")
    private String appId;
}
