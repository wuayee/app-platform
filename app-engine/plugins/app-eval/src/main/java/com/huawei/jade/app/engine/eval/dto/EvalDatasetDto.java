/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.dto;

import com.huawei.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建评估数据集所需参数。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvalDatasetDto {
    @Property(description = "数据集名称")
    private String datasetName;

    @Property(description = "数据集描述")
    private String description;

    @Property(description = "作者")
    private String author;

    @Property(description = "当前的应用id")
    private String appId;

    @Property(description = "需要插入的数据列表")
    private List<EvalDataDto> data = new ArrayList<>();
}
