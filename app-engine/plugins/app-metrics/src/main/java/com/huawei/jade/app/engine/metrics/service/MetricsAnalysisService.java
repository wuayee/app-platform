/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.metrics.service;

import com.huawei.jade.app.engine.metrics.po.TimeType;
import com.huawei.jade.app.engine.metrics.vo.MetricsAnalysisVo;

/**
 * MetricService类消息处理策略
 *
 * @author c00819987
 * @since 2024/05/21
 */
public interface MetricsAnalysisService {
    /**
     * collect data hourly
     */
    void collectAccessData();

    /**
     * 收集feedback面板数据
     *
     * @param appId 应用id
     * @param timeType 根据时间类型计算起始时间
     * @return feedback面板数据
     */
    MetricsAnalysisVo findMetricsData(String appId, TimeType timeType);
}
