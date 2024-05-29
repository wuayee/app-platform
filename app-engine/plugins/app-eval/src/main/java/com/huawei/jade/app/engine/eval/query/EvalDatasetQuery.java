/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.query;

import com.huawei.fit.http.annotation.RequestQuery;
import com.huawei.fitframework.annotation.Property;
import com.huawei.fitframework.validation.constraints.Range;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评估数据查询参数。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvalDatasetQuery {
    @Property(description = "数据集id")
    @RequestQuery(name = "datasetId")
    private long datasetId;

    @Property(description = "页码(1开始)", example = "1")
    @RequestQuery(name = "pageIndex", required = false, defaultValue = "1")
    @Range(min = 1, max = Integer.MAX_VALUE, message = "页码从1开始")
    private int pageIndex = 1;

    @Property(description = "每页大小", example = "10")
    @RequestQuery(name = "pageSize", required = false, defaultValue = "10")
    @Range(min = 1, max = 300, message = "每页尺寸范围[1, 300]")
    private int pageSize = 10;
}
