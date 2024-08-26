/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.metrics.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.Mock;
import com.huawei.fitframework.test.annotation.MvcTest;
import com.huawei.fitframework.test.domain.mvc.MockMvc;
import com.huawei.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import com.huawei.fitframework.test.domain.mvc.request.MockRequestBuilder;
import com.huawei.jade.app.engine.metrics.dto.MetricsFeedbackDto;
import com.huawei.jade.app.engine.metrics.po.TimeType;
import com.huawei.jade.app.engine.metrics.service.MetricsAnalysisService;
import com.huawei.jade.app.engine.metrics.service.MetricsFeedbackService;
import com.huawei.jade.app.engine.metrics.vo.MetricsAnalysisVo;
import com.huawei.jade.app.engine.metrics.vo.MetricsFeedbackVo;
import com.huawei.jade.app.engine.metrics.vo.Page;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * {@link MetricsController} 的测试。
 *
 * @author 刘信宏
 * @since 2024-08-22
 */
@MvcTest(classes = {MetricsController.class})
class MetricsControllerTest {
    @Mock
    private MetricsAnalysisService metricsAnalysisService;

    @Mock
    private MetricsFeedbackService metricsFeedbackService;

    @Fit
    private MetricsController metricsController;

    @Fit
    private MockMvc mockMvc;

    private HttpClassicClientResponse<?> response;

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    void test_getAnalysis_should_ok_when_test_data_combination() {
        // setup
        MetricsAnalysisVo metricsAnalysisVo = new MetricsAnalysisVo();
        when(metricsAnalysisService.findMetricsData(anyString(), any(TimeType.class))).thenReturn(metricsAnalysisVo);

        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/metrics/analysis")
                .param("appId", "1")
                .param("timeType", String.valueOf(TimeType.TODAY))
                .responseType(MetricsAnalysisVo.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    void test_getFeedback_should_ok_when_test_data_combination() {
        Page<MetricsFeedbackVo> page = new Page();
        when(metricsFeedbackService.getMetricsFeedback(any(MetricsFeedbackDto.class))).thenReturn(page);

        MetricsFeedbackDto metricsFeedbackDTO = MetricsFeedbackDto.builder().appId("id").build();
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/metrics/feedback")
                .jsonEntity(metricsFeedbackDTO)
                .responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    void test_export_should_ok_when_test_data_combination() throws Exception {
        // setup
        byte[] buf = new byte[0];
        ByteArrayInputStream inputStream = new ByteArrayInputStream(buf);
        when(metricsFeedbackService.export(any(MetricsFeedbackDto.class))).thenReturn(inputStream);

        MetricsFeedbackDto metricsFeedbackDTO = new MetricsFeedbackDto();

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/metrics/export").jsonEntity(metricsFeedbackDTO).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }
}