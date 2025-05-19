/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.persist.mapper;

import modelengine.fit.waterflow.flowsengine.persist.entity.FlowStreamInfo;
import modelengine.fit.waterflow.flowsengine.persist.po.FlowDefinitionPO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * FlowDefinitionMapper
 * 流程定义MybatisMapper类
 *
 * @author 晏钰坤
 * @since 2023/8/15
 */
@Mapper
public interface FlowDefinitionMapper {
    /**
     * 保存流程定义实体
     *
     * @param flowDefinition {@link FlowDefinitionPO} 流程定义PO对象
     * @param createdAt {@link LocalDateTime} 创建时间
     */
    void create(@Param("flowDefinition") FlowDefinitionPO flowDefinition, @Param("createdAt") LocalDateTime createdAt);

    /**
     * 根据流程id查找对应流程定义对象
     *
     * @param definitionId 流程定义唯一标识id {@link String}
     * @return 流程定义id对应的流程定义PO对象 {@link FlowDefinitionPO}
     */
    FlowDefinitionPO find(@Param("definitionId") String definitionId);

    /**
     * 根据流程id删除对应流程定义对象
     *
     * @param definitionId 流程定义唯一标识id {@link String}
     */
    void delete(@Param("definitionId") String definitionId);

    /**
     * 根据租户id获取最新流程定义列表
     *
     * @param tenantId 租户id标识 {@link String}
     * @return 流程定义PO列表
     */
    List<FlowDefinitionPO> findByTenantId(@Param("tenantId") String tenantId);

    /**
     * 根据流程名称和版本号查询流程定义对象
     *
     * @param name 流程名称 {@link String}
     * @param version 流程版本
     * @return 流程定义PO对象
     */
    FlowDefinitionPO findByFlowNameAndVersion(@Param("name") String name, @Param("version") String version);

    /**
     * 根据metaId和版本号查询流程定义对象
     *
     * @param metaId 流程metaId标识 {@link String}
     * @param version 流程版本
     * @return 流程定义PO对象
     */
    FlowDefinitionPO findByMetaIdAndVersion(@Param("metaId") String metaId, @Param("version") String version);

    /**
     * 根据metaId和一部分版本号查询流程定义对象
     *
     * @param metaId 流程metaId标识 {@link String}
     * @param version 流程版本
     * @return 流程定义PO对象
     */
    List<FlowDefinitionPO> findByMetaIdAndPartVersion(@Param("metaId") String metaId, @Param("version") String version);

    /**
     * 更新流程定义状态
     *
     * @param flowDefinition 流程定义对象
     */
    void update(@Param("flowDefinition") FlowDefinitionPO flowDefinition);

    /**
     * 根据metaId和version列表查询流程定义
     *
     * @param streams 流程定义metaId和version列表
     * @return 流程定义PO对象
     */
    List<FlowDefinitionPO> findByStreamIdList(List<FlowStreamInfo> streams);

    /**
     * findByFitableId
     *
     * @param fitableId fitableId
     * @param offset offset
     * @param limit limit
     * @return List<FlowDefinitionPO>
     */
    List<FlowDefinitionPO> findByFitableId(@Param("fitableId") String fitableId, @Param("offset") Integer offset,
            @Param("limit") Integer limit);

    /**
     * 根据fitable id获取fitable数量
     *
     * @param fitableId fitableId
     * @return 总数
     */
    Integer getCountByFitableId(@Param("fitableId") String fitableId);

    /**
     * 力量查找fitable数量
     *
     * @param fitableIds fitableid列表
     * @return fitable数量集合
     */
    List<Map<String, Object>> selectFitableCounts(@Param("fitableIds") List<String> fitableIds);
}
