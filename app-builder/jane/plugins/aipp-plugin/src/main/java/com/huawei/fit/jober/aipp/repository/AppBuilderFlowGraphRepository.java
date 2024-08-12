/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository;

import com.huawei.fit.jober.aipp.domain.AppBuilderFlowGraph;

import java.util.List;

/**
 * AppBuilder流程图持久化层
 *
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
public interface AppBuilderFlowGraphRepository {
    /**
     * 通过流程图id查询流程图信息
     *
     * @param id 要查询的流程图id
     * @return AppBuilder流程图信息
     */
    AppBuilderFlowGraph selectWithId(String id);

    /**
     * 插入一条流程图信息
     *
     * @param appBuilderFlowGraph 要插入的流程图信息
     */
    void insertOne(AppBuilderFlowGraph appBuilderFlowGraph);

    /**
     * 更新一条流程图信息
     *
     * @param appBuilderFlowGraph 被更新的流程图信息
     */
    void updateOne(AppBuilderFlowGraph appBuilderFlowGraph);

    /**
     * 根据流程图id删除流程图
     *
     * @param ids 被删除的流程图id集合
     */
    void delete(List<String> ids);
}
