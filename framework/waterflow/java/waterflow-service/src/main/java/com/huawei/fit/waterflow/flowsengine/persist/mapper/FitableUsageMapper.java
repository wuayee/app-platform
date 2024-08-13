/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.persist.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * FitableUsage的Mapper映射
 *
 * @author 孙怡菲
 * @since 2023-11-16
 */
@Mapper
public interface FitableUsageMapper {
    /**
     * save
     *
     * @param definitionId definitionId
     * @param fitableIds fitableIds
     */
    void save(@Param("definitionId") String definitionId, @Param("fitableIds") List<String> fitableIds);

    /**
     * deleteByDefinitionId
     *
     * @param definitionId definitionId
     */
    void deleteByDefinitionId(@Param("defintionId") String definitionId);
}
