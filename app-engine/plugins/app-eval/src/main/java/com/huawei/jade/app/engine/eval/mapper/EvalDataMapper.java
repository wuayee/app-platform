/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.mapper;

import com.huawei.jade.app.engine.eval.po.EvalDataPo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * 评估数据相关的db接口。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Mapper
public interface EvalDataMapper {
    /**
     * 插入一条评估数据。
     *
     * @param evalData 表示评估数据实体类的 {@link EvalDataPo}。
     */
    void insert(EvalDataPo evalData);

    /**
     * 批量插入评估数据。
     *
     * @param dataList 表示评估数据列表的 {@link List}{@code <}{@link EvalDataPo}{@code >}。
     */
    void insertAll(List<EvalDataPo> dataList);

    /**
     * 根据id获取评估数据。
     *
     * @param id 表示评估数据id的 {@link Long}。
     * @return 表示评估数据的 {@link EvalDataPo}。
     */
    EvalDataPo getById(long id);

    /**
     * 通过数据集id获取评估数据列表。
     *
     * @param datasetId 表示评估数据集id的 {@link Long}。
     * @param rowBounds 表示分页设置的 {@link RowBounds}。
     * @return 表示数据列表的 {@link List}{@code <}{@link EvalDataPo}{@code >}。
     */
    List<EvalDataPo> getByDatasetId(long datasetId, RowBounds rowBounds);

    /**
     * 根据数据集id获取评估数据列表，不分页。
     *
     * @param datasetId 表示评估数据集id的 {@link Long}。
     * @return 表示数据列表的 {@link List}{@code <}{@link EvalDataPo}{@code >}。
     */
    List<EvalDataPo> getByDatasetId(long datasetId);

    /**
     * 通过数据集id获取评估数据数量。
     *
     * @param datasetId  表示评估数据集id的 {@link Long}。
     * @return 表示数量的 {@link Long}。
     */
    long getCountByDatasetId(long datasetId);

    /**
     * 通过id删除一条评估数据。
     *
     * @param id 表示数据id的 {@link Long}。
     */
    void deleteById(long id);

    /**
     * 通过id更新一条评估数据。
     *
     * @param evalData 表示评估数据实体类的 {@link EvalDataPo}。
     */
    void updateById(EvalDataPo evalData);
}
