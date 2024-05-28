/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评估数据集实体类。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvalDatasetPo {
    private long id;
    private String datasetName;
    private String description;
    private String author;
    private String appId;
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;
}
