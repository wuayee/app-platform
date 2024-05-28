/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.dto;

import com.huawei.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新评估数据集所需参数。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvalDatasetUpdateDto {
    @Property(description = "需要编辑的数据集id")
    private long id;

    @Property(description = "数据集名称")
    private String datasetName;

    @Property(description = "数据集描述")
    private String description;
}
