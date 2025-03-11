/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.jade.app.engine.eval.manager;

import modelengine.jade.app.engine.eval.entity.EvalVersionEntity;

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
     * 获取数据集最新版本号。
     *
     * @param datasetId 表示查询版本的评估数据集唯一标识的 {@link Long}。
     * @return 表示评估数据版本的 {@link EvalVersionEntity}。
     */
    EvalVersionEntity getLatestVersion(Long datasetId);
}