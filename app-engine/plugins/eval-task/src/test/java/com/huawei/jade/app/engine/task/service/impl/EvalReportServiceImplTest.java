/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.huawei.jade.app.engine.task.entity.EvalReportEntity;
import com.huawei.jade.app.engine.task.mapper.EvalReportMapper;
import com.huawei.jade.app.engine.task.service.EvalReportService;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link EvalReportServiceImpl} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-14
 */
@FitTestWithJunit(includeClasses = EvalReportServiceImpl.class)
public class EvalReportServiceImplTest {
    @Fit
    private EvalReportService evalReportService;

    @Mock
    private EvalReportMapper evalReportMapper;

    @AfterEach
    void teardown() {
        clearInvocations(this.evalReportMapper);
    }

    @Test
    @DisplayName("插入评估任务成功")
    void shouldOkWhenCreateEvalTask() {
        doNothing().when(this.evalReportMapper).create(any());
        EvalReportEntity entity = new EvalReportEntity();
        entity.setNodeName("Accuracy");
        entity.setAlgorithmSchema("{}");
        entity.setPassScore(80.0);
        entity.setAverageScore(100.0);
        entity.setHistogram("{}");
        entity.setInstanceId(1L);

        this.evalReportService.createEvalReport(entity);
        verify(this.evalReportMapper, times(1)).create((any()));
    }
}