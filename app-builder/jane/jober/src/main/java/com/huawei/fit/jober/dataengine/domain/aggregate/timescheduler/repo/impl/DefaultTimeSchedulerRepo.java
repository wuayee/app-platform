/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.dataengine.domain.aggregate.timescheduler.repo.impl;

import static com.huawei.fit.jober.common.ErrorCodes.ENTITY_NOT_FOUND;

import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.common.utils.UuidUtil;
import com.huawei.fit.jober.dataengine.domain.aggregate.timescheduler.TimeScheduler;
import com.huawei.fit.jober.dataengine.domain.aggregate.timescheduler.repo.TimeSchedulerRepo;
import com.huawei.fit.jober.dataengine.persist.mapper.TimeSchedulerMapper;
import com.huawei.fit.jober.dataengine.persist.po.TimeSchedulerPo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import lombok.RequiredArgsConstructor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@link TimeSchedulerRepo} 默认实现类
 *
 * @author 晏钰坤
 * @since 2023/6/13
 */
@Component
@RequiredArgsConstructor
public class DefaultTimeSchedulerRepo implements TimeSchedulerRepo {
    private static final Logger log = Logger.get(DefaultTimeSchedulerRepo.class);

    private final TimeSchedulerMapper timeSchedulerMapper;

    @Override
    public TimeScheduler save(TimeScheduler timeScheduler) {
        if (StringUtils.isBlank(timeScheduler.getSchedulerId())) {
            timeScheduler.setSchedulerId(UuidUtil.uuid());
            timeSchedulerMapper.create(this.serializer(timeScheduler));
        } else {
            timeSchedulerMapper.update(this.serializer(timeScheduler));
        }
        return timeScheduler;
    }

    @Override
    public TimeScheduler find(String id) {
        return Optional.ofNullable(timeSchedulerMapper.find(id)).map(this::serializer).orElseThrow(() -> {
            log.error("Cannot find timeScheduler by ID {}.", id);
            return new JobberException(ENTITY_NOT_FOUND, "TimeScheduler", id);
        });
    }

    @Override
    public void delete(TimeScheduler entity) {
        timeSchedulerMapper.delete(entity.getSchedulerId());
    }

    @Override
    public List<TimeScheduler> queryAllScheduler() {
        return timeSchedulerMapper.findAll().stream().map(this::serializer).collect(Collectors.toList());
    }

    @Override
    public TimeScheduler querySchedulerById(String schedulerId) {
        return this.serializer(timeSchedulerMapper.find(schedulerId));
    }

    @Override
    public TimeScheduler queryByTaskSourceId(String taskSourceId) {
        return Optional.ofNullable(timeSchedulerMapper.queryByTaskSourceId(taskSourceId))
                .map(this::serializer)
                .orElseThrow(() -> {
                    log.error("Cannot find timeScheduler by taskSourceID {}.", taskSourceId);
                    return new JobberException(ENTITY_NOT_FOUND, "TimeScheduler", taskSourceId);
                });
    }

    private TimeSchedulerPo serializer(TimeScheduler timeScheduler) {
        return TimeSchedulerPo.builder()
                .schedulerId(timeScheduler.getSchedulerId())
                .taskSourceId(timeScheduler.getTaskSourceId())
                .taskDefinitionId(timeScheduler.getTaskDefinitionId())
                .taskTypeId(timeScheduler.getTaskTypeId())
                .schedulerDataType(timeScheduler.getSchedulerDataType())
                .sourceApp(timeScheduler.getSourceApp())
                .createTime(timeScheduler.getCreateTime())
                .endTime(timeScheduler.getEndTime())
                .schedulerInterval(timeScheduler.getSchedulerInterval())
                .latestExecutorTime(timeScheduler.getLatestExecutorTime())
                .modifyTime(timeScheduler.getModifyTime())
                .filter(timeScheduler.getFilter())
                .properties(JSON.toJSONString(timeScheduler.getProperties()))
                .ownerAddress(timeScheduler.getOwnerAddress())
                .build();
    }

    private TimeScheduler serializer(TimeSchedulerPo timeSchedulerPO) {
        return TimeScheduler.builder()
                .schedulerId(timeSchedulerPO.getSchedulerId())
                .taskSourceId(timeSchedulerPO.getTaskSourceId())
                .taskDefinitionId(timeSchedulerPO.getTaskDefinitionId())
                .taskTypeId(timeSchedulerPO.getTaskTypeId())
                .schedulerDataType(timeSchedulerPO.getSchedulerDataType())
                .sourceApp(timeSchedulerPO.getSourceApp())
                .createTime(timeSchedulerPO.getCreateTime())
                .endTime(timeSchedulerPO.getEndTime())
                .schedulerInterval(timeSchedulerPO.getSchedulerInterval())
                .latestExecutorTime(timeSchedulerPO.getLatestExecutorTime())
                .modifyTime(timeSchedulerPO.getModifyTime())
                .filter(timeSchedulerPO.getFilter())
                .properties(
                        JSON.parseObject(timeSchedulerPO.getProperties(), new TypeReference<Map<String, String>>() {}))
                .ownerAddress(timeSchedulerPO.getOwnerAddress())
                .timeSchedulerRepo(this)
                .build();
    }
}
