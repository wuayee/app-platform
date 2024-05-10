/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.finance;

import java.util.List;

/**
 * AutoGraph
 *
 * @author 易文渊
 * @since 2024-04-27
 */
public class AutoGraph {
    private List<ChartType> chartType;
    private List<String> chartData;
    private List<String> chartTitle;
    private List<String> chartAnswer;
    private String answer;

    public List<ChartType> getChartType() {
        return chartType;
    }

    public void setChartType(List<ChartType> chartType) {
        this.chartType = chartType;
    }

    public List<String> getChartData() {
        return chartData;
    }

    public void setChartData(List<String> chartData) {
        this.chartData = chartData;
    }

    public List<String> getChartTitle() {
        return chartTitle;
    }

    public void setChartTitle(List<String> chartTitle) {
        this.chartTitle = chartTitle;
    }

    public List<String> getChartAnswer() {
        return chartAnswer;
    }

    public void setChartAnswer(List<String> chartAnswer) {
        this.chartAnswer = chartAnswer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}