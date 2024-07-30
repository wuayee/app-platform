/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.finance;

import com.huawei.fitframework.util.MapBuilder;

import java.util.Map;

/**
 * ChartType
 *
 * @author 易文渊
 * @since 2024-04-27
 */
public enum ChartType {
    /**
     * 表格
     */
    TABLE,

    /**
     * 基础柱状图
     */
    BAR,

    /**
     * 基础折线图
     */
    LINE,

    /**
     * 环形图
     */
    PIE,

    /**
     * 预测图
     */
    FORECAST,

    /**
     * 折柱混合
     */
    MIX_LINE_BAR,

    /**
     * 堆叠柱状图
     */
    BAR_STACK,

    /**
     * 默认图
     */
    DEFAULT;

    private static Map<String, ChartType> chartTypeFactory;

    static {
        chartTypeFactory = MapBuilder.<String, ChartType>get()
                .put("table", TABLE)
                .put("bar_chart", BAR)
                .put("line_chart", LINE)
                .put("pie_chart", PIE)
                .put("forecast", FORECAST)
                .put("bar_line_chart", MIX_LINE_BAR)
                .put("stack_line_chart", BAR_STACK)
                .build();
    }

    /**
     * from
     *
     * @param type 类型
     * @return ChartType
     */
    public static ChartType from(String type) {
        return chartTypeFactory.getOrDefault(type, DEFAULT);
    }
}