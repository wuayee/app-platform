/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.metrics.service.impl;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.jade.app.engine.metrics.mapper.ConversationRecordMapper;
import com.huawei.jade.app.engine.metrics.mapper.MetricsAccessMapper;
import com.huawei.jade.app.engine.metrics.po.ConversationRecordPo;
import com.huawei.jade.app.engine.metrics.po.TimeType;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * {@link MetricsAnalysisServiceImpl} 的测试。
 *
 * @author 刘信宏
 * @since 2024-08-21
 */
@FitTestWithJunit(includeClasses = MetricsAnalysisServiceImpl.class)
class MetricsAnalysisServiceImplTest {
    @Mock
    private ConversationRecordMapper conversationRecordMapper;

    @Mock
    private MetricsAccessMapper metricsAccessMapper;

    @Fit
    private MetricsAnalysisServiceImpl metricsAnalysisServiceImpl;

    static class TimeTypeProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Arrays.stream(TimeType.values()).map(Arguments::of);
        }
    }

    @AfterEach
    void tearDown() {
        clearInvocations(this.conversationRecordMapper, this.metricsAccessMapper, this.conversationRecordMapper);
    }

    @Test
    void test_collectAccessData_should_void_when_test_data_combination() {
        List<ConversationRecordPo> metricMessages = new ArrayList<>();
        ConversationRecordPo conversationRecordPo = ConversationRecordPo.builder().id(1L).appId("id").build();
        metricMessages.add(conversationRecordPo);
        when(this.conversationRecordMapper.getRecordByTime(any(LocalDateTime.class),
                any(LocalDateTime.class))).thenReturn(metricMessages);

        this.metricsAnalysisServiceImpl.collectAccessData();

        verify(this.conversationRecordMapper).getRecordByTime(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @ParameterizedTest
    @ArgumentsSource(TimeTypeProvider.class)
    void test_findMetricsData_should_return_not_null_when_objects_is_null(TimeType type) {
        this.metricsAnalysisServiceImpl.findMetricsData("id", type);

        verify(this.conversationRecordMapper).getBasicMetrics(anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class));
        verify(this.conversationRecordMapper).getAvgResponseRange(anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class));
        verify(this.conversationRecordMapper).getTopUsers(anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class));
        if (type == TimeType.TODAY || type == TimeType.YESTERDAY) {
            verify(this.metricsAccessMapper).getHourlyAccessData(anyString(),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class));
        } else {
            verify(this.metricsAccessMapper).getDailyAccessData(anyString(),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class));
        }
    }
}