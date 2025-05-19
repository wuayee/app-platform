/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry;

import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowRetry;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 流程重试 repo核心类
 *
 * @author 李哲峰
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
     * 根据下次重试时间筛选出到期并且不在id列表的重试记录
     *
     * @param time 用于比对的时间
     * @param exceptEntityIds 上下文实体Id列表
     * @return 重试记录列表
     */
    List<FlowRetry> filterByNextRetryTime(LocalDateTime time, List<String> exceptEntityIds);


    /**
     * 批量删除上下文entityIdList对应的重试记录
     *
     * @param entityIdList 上下文实体Id列表
     */
    void delete(List<String> entityIdList);

    /**
     * 获取最近重试的重试数据
     *
     * @param time 当前重试时间
     * @return 下次重试时间
     */
    FlowRetry getNextFlowRetry(LocalDateTime time);

    /**
     * 查询是否存在重试数据
     *
     * @return 重试数据数量
     */
    int hasRetryData();
}
