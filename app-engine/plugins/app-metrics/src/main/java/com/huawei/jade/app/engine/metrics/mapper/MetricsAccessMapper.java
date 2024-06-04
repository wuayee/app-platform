/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.metrics.mapper;

import com.huawei.jade.app.engine.metrics.po.MetricsAccessPo;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * MetricsAccessMapper类消息处理策略
 *
 * @author c00819987
 * @since 2024/05/24
 */
@Mapper
public interface MetricsAccessMapper {
    /**
     * insert to metrics_access in batch
     *
     * @param metricsAccessList metrics_access list
     */
    void insertMetricAccessBatch(@Param("metricsAccessList") List<MetricsAccessPo> metricsAccessList);

    /**
     * get hourly data
     *
     * @param appId 应用id
     * @param startTime 根据时间类型计算起始时间
     * @param endTime 根据时间类型计算结束时间
     * @return 获取以小时为单位的数据
     */
    @MapKey("time_unit")
    List<Map<String, Object>> getHourlyAccessData(
            @Param("appId") String appId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * get daily data
     *
     * @param appId 应用id
     * @param startTime 根据时间类型计算起始时间
     * @param endTime 根据时间类型计算结束时间
     * @return 获取以天为单位的数据
     */
    @MapKey("time_unit")
    List<Map<String, Object>> getDailyAccessData(
            @Param("appId") String appId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
