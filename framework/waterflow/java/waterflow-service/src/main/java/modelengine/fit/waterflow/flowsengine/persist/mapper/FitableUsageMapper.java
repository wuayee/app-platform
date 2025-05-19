/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.persist.mapper;

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
