/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowretry;

import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowRetry;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 流程重试 repo核心类
 *
 * @author l00862071
 * @since 2024/1/31
 */
public interface FlowRetryRepo {
    /**
     * 批量存取重试记录
     *
     * @param flowRetryList 重试记录列表
     */
    void save(List<FlowRetry> flowRetryList);

    /**
     * 批量更新重试记录
     *
     * @param flowRetryList 重试记录列表
     * @return 更新结果
     */
    int updateRetryRecord(List<FlowRetry> flowRetryList);

    /**
     * 批量更新下次重试时间
     *
     * @param entityIdList 上下文实体Id列表
     * @param nextRetryTime 下次重试时间
     */
    void updateNextRetryTime(List<String> entityIdList, LocalDateTime nextRetryTime);

    /**
     * 根据实体Id查询重试记录
     *
     * @param entityId 上下文实体Id
     * @return 重试记录
     */
    FlowRetry getById(String entityId);

    /**
     * 根据下次重试时间筛选出到期的重试记录
     *
     * @param time 用于比对的时间
     * @return 重试记录列表
     */
    List<FlowRetry> filterByNextRetryTime(LocalDateTime time);

    /**
     * 批量删除上下文entityIdList对应的重试记录
     *
     * @param entityIdList 上下文实体Id列表
     */
    void delete(List<String> entityIdList);
}
