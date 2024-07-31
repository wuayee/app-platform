/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.po;

import com.huawei.jade.common.po.BasePo;

import lombok.Data;

/**
 * 评估数据集 ORM 对象。
 *
 * @author 何嘉斌
 * @since 2024-07-31
 */
@Data
public class EvalDatasetPo extends BasePo {
    /**
     * 评估数据集名字。
     */
    private String name;

    /**
     * 评估数据集描述。
     */
    private String description;

    /**
     * 评估数据集数据规范。
     */
    private String schema;

    /**
     * 应用唯一标识。
     */
    private String appId;
}
