/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.manager;

import com.huawei.jade.app.engine.eval.entity.EvalVersionEntity;

import java.util.List;

/**
 * 表示评估数据集版本管理器。
 *
 * @author 易文渊
 * @since 2024-07-20
 */
public interface EvalDatasetVersionManager {
    /**
     * 分配数据集版本号。
     *
     * @return 表示评估数据版本的 {@code long}。
     */
    long applyVersion();

    /**
     * 获取数据集全部版本号。
     *
     * @param datasetId 表示查询版本的评估数据集唯一标识的 {@link Long}。
     * @return 表示评估数据版本的 {@link List}{@code <}{@link EvalVersionEntity}{@code >}。
     */
    List<EvalVersionEntity> getAllVersion(Long datasetId);
}