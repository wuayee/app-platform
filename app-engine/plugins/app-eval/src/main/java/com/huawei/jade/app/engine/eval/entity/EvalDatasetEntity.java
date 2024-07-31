/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.entity;

import lombok.Data;

import java.util.List;

/**
 * 表示评估数据集的实体对象。
 *
 * @author 何嘉斌
 * @since 2024-08-01
 */
@Data
public class EvalDatasetEntity {
    /**
     * 评估数据集名字。
     */
    private String name;

    /**
     * 评估数据集描述。
     */
    private String description;

    /**
     * 评估数据集数据。
     */
    private List<String> contents;

    /**
     * 评估数据集数据规范。
     */
    private String schema;

    /**
     * 应用唯一标识。
     */
    private String appId;
}
