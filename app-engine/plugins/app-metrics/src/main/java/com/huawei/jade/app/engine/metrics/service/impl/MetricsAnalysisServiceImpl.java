/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.metrics.service.impl;

import com.huawei.jade.app.engine.metrics.mapper.ConversationRecordMapper;
import com.huawei.jade.app.engine.metrics.mapper.MetricsAccessMapper;
import com.huawei.jade.app.engine.metrics.po.ConversationRecordPo;
import com.huawei.jade.app.engine.metrics.po.MetricsAccessPo;
import com.huawei.jade.app.engine.metrics.po.TimeType;
import com.huawei.jade.app.engine.metrics.service.MetricsAnalysisService;
import com.huawei.jade.app.engine.metrics.vo.MetricsAnalysisVo;
import com.huawei.jade.app.engine.metrics.vo.UserAccessVo;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.schedule.annotation.Scheduled;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MetricServiceImpl类消息处理策略
 *
 * @author 陈霄宇
 * @since 2024-05-21
 */
@Component
public class MetricsAnalysisServiceImpl implements MetricsAnalysisService {
    @Fit
    private ConversationRecordMapper conversationRecordMapper;

    @Fit
    private MetricsAccessMapper metricsAccessMapper;

    /**
     * collect data hourly
     */
    @Scheduled(strategy = Scheduled.Strategy.CRON, value = "0 0 0/1 * * ?")
    @Override
    public void collectAccessData() {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(1);
        List<ConversationRecordPo> metricMessages = conversationRecordMapper.getRecordByTime(startTime, endTime);
        Map<String, Long> totalAccessByApp =
                metricMessages.stream()
                        .collect(Collectors.groupingBy(ConversationRecordPo::getAppId, Collectors.counting()));

        List<MetricsAccessPo> metricsAccessList = totalAccessByApp.entrySet().stream()
                .map(entry -> {
                    MetricsAccessPo metricAccess = new MetricsAccessPo();
                    metricAccess.setAppId(entry.getKey());
                    metricAccess.setTotalAccess(entry.getValue().intValue());
                    metricAccess.setCreateTime(endTime);
                    return metricAccess;
                })
                .collect(Collectors.toList());

        metricsAccessMapper.insertMetricAccessBatch(metricsAccessList);
    }

    /**
     * 收集feedback面板数据
     *
     * @param appId 应用id
     * @param timeType 根据时间类型计算起始时间
     * @return feedback面板数据
     */
    @Override
    public MetricsAnalysisVo findMetricsData(String appId, TimeType timeType) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = calculateStartTime(timeType, now);
        LocalDateTime endTime = calculateEndTime(timeType, now);

        Map<String, Object> basicMetrics = conversationRecordMapper.getBasicMetrics(appId, startTime, endTime);
        Map<String, Object> avgResponseRange = conversationRecordMapper.getAvgResponseRange(appId, startTime, endTime);
        List<UserAccessVo> topUsers = conversationRecordMapper.getTopUsers(appId, startTime, endTime);
        List<Map<String, Object>> userAccessData = getUserAccessData(timeType, appId, startTime, endTime);

        MetricsAnalysisVo metricsAnalysisVO = new MetricsAnalysisVo();
        metricsAnalysisVO.setBasicMetrics(basicMetrics);
        metricsAnalysisVO.setAvgResponseRange(avgResponseRange);
        metricsAnalysisVO.setTopUsers(topUsers);
        metricsAnalysisVO.setUserAccessData(userAccessData);
        return metricsAnalysisVO;
    }

    private List<Map<String, Object>> getUserAccessData(TimeType timeType, String appId,
                                                        LocalDateTime startTime, LocalDateTime endTime) {
        if (timeType == TimeType.TODAY || timeType == TimeType.YESTERDAY) {
            return metricsAccessMapper.getHourlyAccessData(appId, startTime, endTime);
        } else {
            return metricsAccessMapper.getDailyAccessData(appId, startTime, endTime);
        }
    }

    private LocalDateTime calculateStartTime(TimeType timeType, LocalDateTime now) {
        // 根据 TimeType 计算开始时间
        switch (timeType) {
            case TODAY:
                return now.withHour(0).withMinute(0).withSecond(0).withNano(0);
            case YESTERDAY:
                return now.minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case LAST_7_DAYS:
                return now.minusDays(7);
            case LAST_30_DAYS:
                return now.minusDays(30);
            case THIS_WEEK:
                return now.with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case LAST_WEEK:
                return now.minusWeeks(1).with(DayOfWeek.MONDAY).withHour(0)
                        .withMinute(0).withSecond(0).withNano(0);
            case THIS_MONTH:
                return now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case LAST_MONTH:
                return now.minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0)
                        .withSecond(0).withNano(0);
            default:
                return now;
        }
    }

    private LocalDateTime calculateEndTime(TimeType timeType, LocalDateTime now) {
        switch (timeType) {
            case YESTERDAY:
                return now.minusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            case LAST_WEEK:
                return now.minusWeeks(1).with(DayOfWeek.SUNDAY).withHour(23).withMinute(59).withSecond(59)
                        .withNano(999999999);
            case LAST_MONTH:
                return now.minusMonths(1).withDayOfMonth(now.minusMonths(1).toLocalDate().lengthOfMonth())
                        .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            default:
                return now;
        }
    }
}
