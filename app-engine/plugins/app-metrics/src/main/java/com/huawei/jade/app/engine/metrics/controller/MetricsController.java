/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.metrics.controller;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.entity.FileEntity;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.jade.app.engine.metrics.dto.MetricsFeedbackDto;
import com.huawei.jade.app.engine.metrics.po.TimeType;
import com.huawei.jade.app.engine.metrics.service.MetricsAnalysisService;
import com.huawei.jade.app.engine.metrics.service.MetricsFeedbackService;
import com.huawei.jade.app.engine.metrics.vo.MetricsAnalysisVo;
import com.huawei.jade.app.engine.metrics.vo.MetricsFeedbackVo;
import com.huawei.jade.app.engine.metrics.vo.Page;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * MetricsController类消息处理策略
 *
 * @author 陈霄宇
 * @since 2024/05/21
 */
@Component
@RequestMapping(path = "/metrics", group = "分析与反馈看板相关接口")
public class MetricsController {
    @Fit
    private MetricsAnalysisService metricsAnalysisService;

    @Fit
    private MetricsFeedbackService metricsFeedbackService;

    /**
     * 获取分析看板数据
     *
     * @param appId 应用id
     * @param timeType 时间枚举类
     * @return 分析看板数据
     */
    @GetMapping(path = "/analysis", description = "获取分析数据接口")
    public MetricsAnalysisVo getAnalysis(
            @RequestParam("appId") String appId, @RequestParam("timeType") TimeType timeType) {
        return metricsAnalysisService.findMetricsData(appId, timeType);
    }

    /**
     * 获取反馈看板数据
     *
     * @param metricsFeedbackDTO 用户输入
     * @return 反馈看板数据
     */
    @PostMapping(path = "/feedback", description = "获取反馈数据接口")
    public Page<MetricsFeedbackVo> getFeedback(@RequestBody MetricsFeedbackDto metricsFeedbackDTO) {
        return metricsFeedbackService.getMetricsFeedback(metricsFeedbackDTO);
    }

    /**
     * 下载反馈看板数据
     *
     * @param response 前端
     * @param metricsFeedbackDTO 用户输入
     * @return 三个推荐问题列表
     * @throws IOException 异常捕获
     */
    @PostMapping(path = "/export", description = "下载反馈数据接口")
    public FileEntity export(HttpClassicServerResponse response, @RequestBody MetricsFeedbackDto metricsFeedbackDTO)
            throws IOException {
        try (ByteArrayInputStream inputStream = metricsFeedbackService.export(metricsFeedbackDTO)) {
            return FileEntity.createAttachment(response, "问答记录.xls", inputStream, inputStream.available());
        }
    }
}
