/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.service;

import com.huawei.jade.app.engine.eval.entity.EvalDatasetEntity;

/**
 * 表示评估数据集服务。
 *
 * @author 何嘉斌
 * @since 2024-07-31
 */
public interface EvalDatasetService {
    /**
     * 插入评估数据集。
     *
     * @param entity 评估数据集业务对象的 {@link EvalDatasetEntity}。
     */
    void create(EvalDatasetEntity entity);
}
