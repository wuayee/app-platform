/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.repo;

import lombok.RequiredArgsConstructor;
import modelengine.fit.waterflow.common.Constant;
import modelengine.fit.waterflow.flowsengine.persist.entity.FlowStreamInfo;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowDefinitionMapper;
import modelengine.fit.waterflow.flowsengine.persist.po.FlowDefinitionPO;
import modelengine.fitframework.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 查询流程定义repo
 *
 * @author 杨祥宇
 * @since 2023/10/17
 */
@Component
@RequiredArgsConstructor
public class QueryFlowDefinitionRepo {
    private final FlowDefinitionMapper flowDefinitionMapper;

    /**
     * findByStreamId
     *
     * @param streamId streamId
     * @return FlowDefinitionPO
     */
    public FlowDefinitionPO findByStreamId(String streamId) {
        String[] ids = streamId.split(String.valueOf(Constant.STREAM_ID_SEPARATOR));
        String metaId = ids[0];
        String version = ids[1];
        return flowDefinitionMapper.findByMetaIdAndVersion(metaId, version);
    }

    /**
     * findByStreamId
     *
     * @param streamIds streamIds
     * @return List<FlowDefinitionPO>
     */
    public List<FlowDefinitionPO> findByStreamId(List<String> streamIds) {
        List<FlowStreamInfo> streams = new ArrayList<>();
        streamIds.stream().forEach(s -> {
            String[] ids = s.split(String.valueOf(Constant.STREAM_ID_SEPARATOR));
            String metaId = ids[0];
            String version = ids[1];
            streams.add(new FlowStreamInfo(metaId, version));
        });

        return flowDefinitionMapper.findByStreamIdList(streams);
    }

    /**
     * findByFitableId
     *
     * @param fitableId fitableId
     * @param offset offset
     * @param limit limit
     * @return List<FlowDefinitionPO>
     */
    public List<FlowDefinitionPO> findByFitableId(String fitableId, Integer offset, Integer limit) {
        return flowDefinitionMapper.findByFitableId(fitableId, offset, limit);
    }

    /**
     * getCountByFitableId
     *
     * @param fitableId fitableId
     * @return Integer
     */
    public Integer getCountByFitableId(String fitableId) {
        return flowDefinitionMapper.getCountByFitableId(fitableId);
    }

    /**
     * selectFitableCounts
     *
     * @param fitableIds fitableIds
     * @return List<Map < String, Object>>
     */
    public List<Map<String, Object>> selectFitableCounts(List<String> fitableIds) {
        return flowDefinitionMapper.selectFitableCounts(fitableIds);
    }

    /**
     * findByMetaIdAndVersion
     *
     * @param metaId metaId
     * @param version version
     * @return FlowDefinitionPO
     */
    public FlowDefinitionPO findByMetaIdAndVersion(String metaId, String version) {
        return flowDefinitionMapper.findByMetaIdAndVersion(metaId, version);
    }

    /**
     * findByTenant
     *
     * @param tenant tenant
     * @return List<FlowDefinitionPO>
     */
    public List<FlowDefinitionPO> findByTenant(String tenant) {
        return flowDefinitionMapper.findByTenantId(tenant);
    }

    /**
     * 根据元数据ID和部分版本号查询流程定义
     *
     * @param metaId 元数据ID
     * @param version 部分版本号
     * @return 流程定义列表
     */
    public List<FlowDefinitionPO> findByMetaIdAndPartVersion(String metaId, String version) {
        return flowDefinitionMapper.findByMetaIdAndPartVersion(metaId, version);
    }
}
