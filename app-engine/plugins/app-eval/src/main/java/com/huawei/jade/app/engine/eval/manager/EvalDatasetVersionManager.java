/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.manager;

/**
 * 表示评估数据集版本管理器。
 *
 * @author 易文渊
 * @since 2024-07-20
 */
public interface EvalDatasetVersionManager {
    /**
     * 分配版本号。
     *
     * @return 表示评估数据版本的 {@code long}。
     */
    long applyVersion();
}