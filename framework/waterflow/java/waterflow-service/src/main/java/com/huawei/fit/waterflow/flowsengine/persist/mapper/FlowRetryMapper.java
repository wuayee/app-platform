/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.persist.mapper;

import com.huawei.fit.waterflow.flowsengine.persist.po.FlowRetryPO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * flow retry对应MybatisMapper类
 *
 * @author 李哲峰
 * @since 2024/1/31
 */
@Mapper
public interface FlowRetryMapper {
    /**
     * 批量保存流程自动任务重试记录实体
     *
     * @param flowRetryList {@link FlowRetryPO} 流程自动任务重试记录PO对象列表
     */
    void batchCreate(@Param("flowRetryList") List<FlowRetryPO> flowRetryList);

    /**
     * 根据流程上下文entityId查找对应流程自动任务重试记录PO对象
     *
     * @param entityId 流程上下文entityId {@link String}
     * @return 流程上下文entityId对应的流程自动任务重试记录PO对象 {@link FlowRetryPO}
     */
    FlowRetryPO find(@Param("entityId") String entityId);

    /**
     * 根据下次重试时间筛选出到期并且不在id列表中的重试记录
     *
     * @param time 用于比对的时间
     * @param exceptEntityIds 流程上下文entityId列表
     * @return 重试记录PO对象列表
     */
    List<FlowRetryPO> filterByNextRetryTime(@Param("time") LocalDateTime time, List<String> exceptEntityIds);


    /**
     * 批量更新流程自动任务重试记录
     *
     * @param flowRetryList {@link FlowRetryPO} 流程自动任务重试记录PO对象
     * @return 更新结果
     */
    int batchUpdateRetryRecord(@Param("flowRetryList") List<FlowRetryPO> flowRetryList);

    /**
     * 批量更新流程自动任务重试记录下次重试时间
     *
     * @param entityIdList 流程上下文entityId列表 {@link String}
     * @param nextRetryTime 下次重试时间
     */
    void batchUpdateNextRetryTime(@Param("entityIdList") List<String> entityIdList,
            @Param("nextRetryTime") LocalDateTime nextRetryTime);

    /**
     * 根据流程上下文entityIdList批量删除对应流程自动任务重试记录对象
     *
     * @param entityIdList 流程上下文entityId列表 {@link String}
     */
    void batchDelete(@Param("entityIdList") List<String> entityIdList);

    /**
     * 获取下一次需要重试的数据
     *
     * @param time 时间
     * @return retry数据
     */
    FlowRetryPO getNextFlowRetry(LocalDateTime time);

    /**
     * 检查当前是否存在重试数据
     *
     * @return 表中数据行数
     */
    int hasRetryData();
}
