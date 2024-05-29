/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.mapper;

import com.huawei.jade.app.engine.eval.po.EvalDatasetPo;
import com.huawei.jade.app.engine.eval.query.EvalDatasetListQuery;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * 评估数据集相关的db接口。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Mapper
public interface EvalDatasetMapper {
    /**
     * 插入一个评估数据集。
     *
     * @param evalDataset 表示评估数据集的实体类的 {@link EvalDatasetPo}。
     */
    void insert(EvalDatasetPo evalDataset);

    /**
     * 通过id获取一个评估数据集。
     *
     * @param id 表示数据集id的 {@link Long}。
     * @return 表示评估数据集的 {@link EvalDatasetPo}。
     */
    EvalDatasetPo getById(long id);

    /**
     * 根据id删除一个数据集。
     *
     * @param id 表示评估数据集id的 {@link Long}。
     */
    void deleteById(long id);

    /**
     * 通过条件获取评估数据集列表。
     *
     * @param evalDatasetListQuery 表示筛选条件的 {@link EvalDatasetListQuery}。
     * @param rowBounds 表示分页设置的 {@link RowBounds}
     * @return 表示评估数据集列表的 {@link List}{@code <}{@link EvalDatasetPo}{@code >}。
     */
    List<EvalDatasetPo> getByConditions(EvalDatasetListQuery evalDatasetListQuery, RowBounds rowBounds);

    /**
     * 通过id更新一条数据集。
     *
     * @param evalDatasetPO 表示评估数据集实体类的 {@link EvalDatasetPo}
     */
    void updateById(EvalDatasetPo evalDatasetPO);

    /**
     * 通过条件获取评估数据集的数量。
     *
     * @param evalDatasetListQuery 表示筛选条件的 {@link EvalDatasetListQuery}。
     * @return 表示符合条件的数据集的数量的 {@link Long}
     */
    long getCountByConditions(EvalDatasetListQuery evalDatasetListQuery);
}
