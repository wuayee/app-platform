/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.metrics.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import com.huawei.jade.app.engine.metrics.dto.MetricsFeedbackDto;
import com.huawei.jade.app.engine.metrics.mapper.ConversationRecordMapper;
import com.huawei.jade.app.engine.metrics.vo.MetricsFeedbackVo;
import com.huawei.jade.app.engine.metrics.vo.Page;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link MetricsFeedbackServiceImpl} 的测试。
 *
 * @author 刘信宏
 * @since 2024-08-21
 */
@FitTestWithJunit(includeClasses = MetricsFeedbackServiceImpl.class)
class MetricsFeedbackServiceImplTest {
    @Mock
    private ConversationRecordMapper conversationRecordMapper;

    @Fit
    private MetricsFeedbackServiceImpl metricsFeedbackServiceImpl;

    @Test
    void test_getMetricsFeedback_should_ok_when_test_data_combination() {
        // setup
        when(conversationRecordMapper.getCountByCondition(any(MetricsFeedbackDto.class))).thenReturn(5L);
        List<MetricsFeedbackVo> conversations = new ArrayList<>();
        MetricsFeedbackVo metricsFeedbackVo = new MetricsFeedbackVo();
        conversations.add(metricsFeedbackVo);
        when(conversationRecordMapper.getByCondition(any(MetricsFeedbackDto.class))).thenReturn(conversations);

        MetricsFeedbackDto metricsFeedbackDTO = new MetricsFeedbackDto();
        metricsFeedbackDTO.setPageIndex(0);
        metricsFeedbackDTO.setPageSize(10);

        Page<MetricsFeedbackVo> result = metricsFeedbackServiceImpl.getMetricsFeedback(metricsFeedbackDTO);

        assertEquals(5, result.getTotal());
        assertEquals(1, result.getData().size());
    }

    @Test
    void test_export_should_return_not_null_when_test_data_combination() throws IOException {
        // setup
        when(conversationRecordMapper.getCountByCondition(any(MetricsFeedbackDto.class))).thenReturn(5L);
        MetricsFeedbackVo metricsFeedbackVo = new MetricsFeedbackVo();
        metricsFeedbackVo.setResponseTime(10L);
        List<MetricsFeedbackVo> conversations = Collections.singletonList(metricsFeedbackVo);
        when(conversationRecordMapper.getByCondition(any(MetricsFeedbackDto.class))).thenReturn(conversations);

        MetricsFeedbackDto metricsFeedbackDTO = new MetricsFeedbackDto();
        metricsFeedbackDTO.setPageIndex(0);
        metricsFeedbackDTO.setPageSize(10);

        // run the test
        ByteArrayInputStream result = metricsFeedbackServiceImpl.export(metricsFeedbackDTO);

        // verify the results
        assertNotNull(result);
    }
}