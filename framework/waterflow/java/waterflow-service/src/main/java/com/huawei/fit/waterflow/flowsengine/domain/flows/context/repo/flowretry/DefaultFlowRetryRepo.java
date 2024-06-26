/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry;

import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowRetry;
import com.huawei.fit.waterflow.flowsengine.persist.mapper.FlowRetryMapper;
import com.huawei.fit.waterflow.flowsengine.persist.po.FlowRetryPO;
import com.huawei.fitframework.annotation.Component;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@link FlowRetryRepo}默认实现类
 *
 * @author l00862071
 * @since 2024/1/31
 */
@Component
@RequiredArgsConstructor
public class DefaultFlowRetryRepo implements FlowRetryRepo {
    private final FlowRetryMapper flowRetryMapper;

    @Override
    public void save(List<FlowRetry> flowRetryList) {
        List<FlowRetryPO> flowRetryPOS = flowRetryList.stream().map(this::serializer).collect(Collectors.toList());
        flowRetryMapper.batchCreate(flowRetryPOS);
    }

    @Override
    public int updateRetryRecord(List<FlowRetry> flowRetryList) {
        List<FlowRetryPO> flowRetryPOS = flowRetryList.stream().map(this::serializer).collect(Collectors.toList());
        return flowRetryMapper.batchUpdateRetryRecord(flowRetryPOS);
    }

    @Override
    public void updateNextRetryTime(List<String> entityIdList, LocalDateTime nextRetryTime) {
        flowRetryMapper.batchUpdateNextRetryTime(entityIdList, nextRetryTime);
    }

    @Override
    public FlowRetry getById(String entityId) {
        return Optional.ofNullable(flowRetryMapper.find(entityId)).map(this::serializer).orElse(null);
    }

    @Override
    public List<FlowRetry> filterByNextRetryTime(LocalDateTime time) {
        List<FlowRetryPO> pos = flowRetryMapper.filterByNextRetryTime(time);
        return pos.stream().map(this::serializer).collect(Collectors.toList());
    }

    @Override
    public void delete(List<String> entityIdList) {
        flowRetryMapper.batchDelete(entityIdList);
    }

    private FlowRetryPO serializer(FlowRetry flowRetry) {
        return FlowRetryPO.builder()
                .entityId(flowRetry.getEntityId())
                .entityType(flowRetry.getEntityType())
                .nextRetryTime(flowRetry.getNextRetryTime())
                .lastRetryTime(flowRetry.getLastRetryTime())
                .retryCount(flowRetry.getRetryCount())
                .version(flowRetry.getVersion())
                .build();
    }

    private FlowRetry serializer(FlowRetryPO po) {
        return new FlowRetry(po.getEntityId(), po.getEntityType(), po.getNextRetryTime(), po.getLastRetryTime(),
                po.getRetryCount(), po.getVersion());
    }
}
