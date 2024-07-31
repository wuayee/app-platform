/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.mapper;

import com.huawei.jade.app.engine.eval.dto.EvalDatasetQueryParam;
import com.huawei.jade.app.engine.eval.entity.EvalDatasetEntity;
import com.huawei.jade.app.engine.eval.po.EvalDatasetPo;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 表示评估数据集持久层接口。
 *
 * @author 兰宇晨 l00816134
 * @since 2024-07-27
 */
@Mapper
public interface EvalDatasetMapper {
    /**
     * 创建评估数据集。
     *
     * @param evalDatasetPo 表示评估数据集的 {@link EvalDatasetPo}。
     */
    void create(EvalDatasetPo evalDatasetPo);

    /**
     * 表示获取数据集数据规范。
     *
     * @param datasetId 表示评估数据查询参数的 {@link Long}。
     * @return 表示数据集对应的 Schema 的 {@link String}。
     */
    String getSchema(Long datasetId);

    /**
     * 分页查询评估数据集元数据。
     *
     * @param queryParam 表示评估数据集查询参数的 {@link EvalDatasetQueryParam}。
     * @return 表示评估数据集元数据查询结果的 {@link List}{@code <}{@link EvalDatasetEntity}{@code >}。
     */
    List<EvalDatasetEntity> listEvalDataset(EvalDatasetQueryParam queryParam);

    /**
     * 根据数据集 ID 查询评估数据集元数据。
     *
     * @param datasetId 表示评估数据集查询参数的 {@link Long}。
     * @return 表示评估数据集元数据查询结果的 {@link EvalDatasetEntity}。
     */
    EvalDatasetEntity getEvalDatasetById(Long datasetId);

    /**
     * 统计评估数据集数量。
     *
     * @param queryParam 表示评估数据集查询参数的 {@link EvalDatasetQueryParam}。
     * @return 表示评估数据集统计结果的 {@code int}。
     */
    int countEvalDataset(EvalDatasetQueryParam queryParam);
}
