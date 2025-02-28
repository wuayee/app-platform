/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry;

import lombok.RequiredArgsConstructor;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowRetry;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowRetryMapper;
import modelengine.fit.waterflow.flowsengine.persist.po.FlowRetryPO;
import modelengine.fitframework.annotation.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@link FlowRetryRepo}默认实现类
 *
 * @author 李哲峰
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
    public List<FlowRetry> filterByNextRetryTime(LocalDateTime time, List<String> exceptEntityIds) {
        List<FlowRetryPO> pos = flowRetryMapper.filterByNextRetryTime(time, exceptEntityIds);
        return pos.stream().map(this::serializer).collect(Collectors.toList());
    }

    @Override
    public void delete(List<String> entityIdList) {
        if (entityIdList.isEmpty()) {
            return;
        }
        flowRetryMapper.batchDelete(entityIdList);
    }

    @Override
    public FlowRetry getNextFlowRetry(LocalDateTime time) {
        FlowRetryPO flowRetryPO = flowRetryMapper.getNextFlowRetry(time);
        if (flowRetryPO == null) {
            return null;
        }
        return this.serializer(flowRetryPO);
    }

    @Override
    public int hasRetryData() {
        return flowRetryMapper.hasRetryData();
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
