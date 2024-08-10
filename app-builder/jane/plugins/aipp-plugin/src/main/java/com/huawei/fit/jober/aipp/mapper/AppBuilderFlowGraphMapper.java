/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.mapper;

import com.huawei.fit.jober.aipp.po.AppBuilderFlowGraphPo;

import java.util.List;

/**
 * AppBuilder流程图映射器
 *
 * @author 邬涨财 w00575064
 * @since 2024-04-16
 */
public interface AppBuilderFlowGraphMapper {
    /**
     * 通过流程图id查询流程图信息
     *
     * @param id 要查询的流程图id
     * @return AppBuilder流程图信息
     */
    AppBuilderFlowGraphPo selectWithId(String id);

    /**
     * 插入一条流程图信息
     *
     * @param insert 要插入的流程图信息
     */
    void insertOne(AppBuilderFlowGraphPo insert);

    /**
     * 更新一条流程图信息
     *
     * @param update 被更新的流程图信息
     */
    void updateOne(AppBuilderFlowGraphPo update);

    /**
     * 根据流程图id删除流程图
     *
     * @param ids 被删除的流程图id
     */
    void delete(List<String> ids);
}
