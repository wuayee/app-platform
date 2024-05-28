/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评估数据集和评估任务关联表实体类。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvalTaskDatasetPo {
    private long evalTaskId;
    private long evalDatasetId;
}
