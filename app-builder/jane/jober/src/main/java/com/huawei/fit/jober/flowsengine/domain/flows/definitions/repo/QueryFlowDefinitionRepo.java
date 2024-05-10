/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.definitions.repo;

import static com.huawei.fit.jober.common.Constant.STREAM_ID_SEPARATOR;

import com.huawei.fit.jober.flowsengine.persist.entity.FlowStreamInfo;
import com.huawei.fit.jober.flowsengine.persist.mapper.FlowDefinitionMapper;
import com.huawei.fit.jober.flowsengine.persist.po.FlowDefinitionPO;
import com.huawei.fitframework.annotation.Component;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 查询流程定义repo
 *
 * @author y00679285
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
        String[] ids = streamId.split(String.valueOf(STREAM_ID_SEPARATOR));
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
            String[] ids = s.split(String.valueOf(STREAM_ID_SEPARATOR));
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

    public List<FlowDefinitionPO> findByMetaIdAndPartVersion(String metaId, String version) {
        return flowDefinitionMapper.findByMetaIdAndPartVersion(metaId, version);
    }
}
