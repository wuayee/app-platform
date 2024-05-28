/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.mapper;

import com.huawei.jade.app.engine.eval.po.EvalTaskDatasetPo;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 评估数据集和评估任务关联表的db接口。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Mapper
public interface EvalTaskDatasetMapper {
    /**
     * 插入一条评估任务和数据集的关联关系。
     *
     * @param evalTaskDatasetPO 表示评估任务和数据集的关联关系的 {@link EvalTaskDatasetPo}。
     */
    void insert(EvalTaskDatasetPo evalTaskDatasetPO);

    /**
     * 批量插入评估任务和数据集的关联关系。
     *
     * @param evalTaskDatasetPO 表示评估任务和数据集的关联关系列表的 {@link List}{@code <}{@link EvalTaskDatasetPo}{@code >}。
     */
    void insertAll(List<EvalTaskDatasetPo> evalTaskDatasetPO);

    /**
     * 通过任务id获取评估任务和数据集的关联关系列表。
     *
     * @param evalTaskId 表示任务id的 {@link Long}。
     * @return 表示评估任务和数据集的关联关系列表的 {@link List}{@code <}{@link EvalTaskDatasetPo}{@code >}。
     */
    List<EvalTaskDatasetPo> getByEvalTaskId(long evalTaskId);
}
