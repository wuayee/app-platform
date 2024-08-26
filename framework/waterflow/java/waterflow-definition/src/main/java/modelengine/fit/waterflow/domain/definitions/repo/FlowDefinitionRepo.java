/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.definitions.repo;

import modelengine.fit.waterflow.domain.definitions.FlowDefinition;

import java.util.List;

/**
 * FlowDefinitionRepo
 * 流程定义Repo
 *
 * @author 晏钰坤
 * @since 1.0
 */
public interface FlowDefinitionRepo {
    /**
     * 根据前端json数据保存流程定义
     *
     * @param entity 流程定义 {@link FlowDefinition}
     * @param graphData 前端json数据
     * @return {@link FlowDefinition}
     */
    FlowDefinition save(FlowDefinition entity, String graphData);

    /**
     * 根据流程定义ID查找流程定义
     *
     * @param definitionId 流程定义ID
     * @return {@link FlowDefinition}
     */
    FlowDefinition find(String definitionId);

    /**
     * 删除一个流程定义
     *
     * @param flowDefinitionId 流程定义id标识
     */
    void delete(String flowDefinitionId);

    /**
     * 根据streamId查询流程定义
     *
     * @param streamId 流程定义标识 {@link String}
     * @return 流程定义
     */
    FlowDefinition findByStreamId(String streamId);

    /**
     * 更新流程定义
     *
     * @param flowDefinition 流程定义对象 {@link FlowDefinition}
     * @param graphData 流程定义json数据
     */
    void update(FlowDefinition flowDefinition, String graphData);

    /**
     * 根据metaId和version查询流程定义
     *
     * @param metaId 流程定义的metaId {@link String}
     * @param version 流程定义版本 {@link String}
     * @return 流程定义 {@link FlowDefinition}
     */
    FlowDefinition findByMetaIdAndVersion(String metaId, String version);

    /**
     * 批量查询流程定义
     *
     * @param streamIds 流程唯一标识
     * @return 流程定义列表
     */
    List<FlowDefinition> findByStreamIdList(List<String> streamIds);
}
