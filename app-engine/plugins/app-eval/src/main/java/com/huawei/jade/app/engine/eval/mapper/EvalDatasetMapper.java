/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.mapper;

import org.mapstruct.Mapper;

/**
 * 表示评估数据集持久层接口。
 *
 * @author 兰宇晨 l00816134
 * @since 2024-07-27
 */
@Mapper
public interface EvalDatasetMapper {
    /**
     * 表示获取数据集 Schema。
     *
     * @param datasetId 表示评估数据查询参数的 {@link Long}。
     * @return 表示数据集对应的 Schema 的 {@link String}。
     */
    String getSchema(Long datasetId);
}
