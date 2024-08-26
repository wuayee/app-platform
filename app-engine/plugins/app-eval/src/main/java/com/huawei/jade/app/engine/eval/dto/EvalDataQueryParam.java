/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.dto;

import modelengine.fitframework.annotation.Property;
import com.huawei.jade.common.query.PageQueryParam;

import lombok.Data;

/**
 * 表示数据查询参数。
 *
 * @author 兰宇晨
 * @see com.huawei.jade.app.engine.eval.controller.EvalDataController#queryEvalData。
 * @since 2024-07-24
 */
@Data
public class EvalDataQueryParam extends PageQueryParam {
    @Property(description = "数据集编号", required = true)
    private Long datasetId;

    @Property(description = "版本号", required = true)
    private Long version;
}
