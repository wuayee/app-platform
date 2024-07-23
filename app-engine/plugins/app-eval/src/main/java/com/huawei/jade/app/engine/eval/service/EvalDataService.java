/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.service;

import java.util.List;

/**
 * 表示评估数据服务。
 *
 * @author 易文渊
 * @since 2024-07-19
 */
public interface EvalDataService {
    /**
     * 批量插入评估数据。
     *
     * @param datasetId 表示评估数据集编号的 {@link Long}。
     * @param contents 表示评估内容集合的 {@link List}{@code <}{@link String}{@code >}。
     */
    void insertAll(Long datasetId, List<String> contents);
}