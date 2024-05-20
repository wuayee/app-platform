/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository;

import com.huawei.fit.jober.aipp.domain.AppBuilderFlowGraph;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
public interface AppBuilderFlowGraphRepository {
    AppBuilderFlowGraph selectWithId(String id);

    void insertOne(AppBuilderFlowGraph appBuilderFlowGraph);

    void updateOne(AppBuilderFlowGraph appBuilderFlowGraph);

    void delete(String id);
}
