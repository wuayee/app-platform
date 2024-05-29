/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.jade.app.engine.metrics.mapper;

import com.huawei.jade.app.engine.metrics.dto.MetricsFeedbackDto;
import com.huawei.jade.app.engine.metrics.po.ConversationRecordPo;
import com.huawei.jade.app.engine.metrics.vo.MetricsFeedbackVo;
import com.huawei.jade.app.engine.metrics.vo.UserAccessVo;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * MetricsDataMapper类消息处理策略
 *
 * @author c00819987
 * @since 2024/05/23
 */
@Mapper
public interface ConversationRecordMapper {
    /**
     * get total_requests, total_active_users and average_response_time
     *
     * @param appId 应用id
     * @param startTime 根据时间类型计算起始时间
     * @param endTime 根据时间类型计算结束时间
     * @return 哈希表返回total_requests, total_active_users and average_response_time
     */
    @MapKey("metric")
    Map<String, Object> getBasicMetrics(
            @Param("appId") String appId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * get avg response time range
     *
     * @param appId 应用id
     * @param startTime 根据时间类型计算起始时间
     * @param endTime 根据时间类型计算结束时间
     * @return 哈希表返回avg response time range
     */
    @MapKey("range")
    Map<String, Object> getAvgResponseRange(
            @Param("appId") String appId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * get Top 5 user
     *
     * @param appId 应用id
     * @param startTime 根据时间类型计算起始时间
     * @param endTime 根据时间类型计算结束时间
     * @return Top 5 user
     */
    List<UserAccessVo> getTopUsers(
            @Param("appId") String appId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * get conversation_record data filtered by time
     *
     * @param startTime 根据时间类型计算起始时间
     * @param endTime 根据时间类型计算结束时间
     * @return get conversation_record data filtered by time
     */
    List<ConversationRecordPo> getRecordByTime(
            @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * insert into conversation_record
     *
     * @param conversationRecordPo conversation_record字段
     */
    void insertConversationRecord(ConversationRecordPo conversationRecordPo);

    /**
     * get feedback data by condition
     *
     * @param metricsFeedbackDTO 筛选及检索条件
     * @return 过滤和检索后的feedback数据
     */
    List<MetricsFeedbackVo> getByCondition(@Param("metricsFeedbackDTO") MetricsFeedbackDto metricsFeedbackDTO);

    /**
     * get total count after filtered condition
     *
     * @param metricsFeedbackDTO 筛选及检索条件
     * @return 总数
     */
    Long getCountByCondition(MetricsFeedbackDto metricsFeedbackDTO);
}
