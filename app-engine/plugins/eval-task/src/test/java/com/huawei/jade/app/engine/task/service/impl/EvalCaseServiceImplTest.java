/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.FitTestWithJunit;
import com.huawei.fitframework.test.annotation.Mock;
import com.huawei.jade.app.engine.task.entity.EvalCaseEntity;
import com.huawei.jade.app.engine.task.entity.EvalRecordEntity;
import com.huawei.jade.app.engine.task.mapper.EvalCaseMapper;
import com.huawei.jade.app.engine.task.service.EvalCaseService;
import com.huawei.jade.app.engine.task.service.EvalRecordService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * 表示 {@link EvalCaseServiceImpl} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@FitTestWithJunit(includeClasses = EvalCaseServiceImpl.class)
public class EvalCaseServiceImplTest {
    @Fit
    private EvalCaseService evalCaseService;

    @Mock
    private EvalRecordService evalRecordService;

    @Mock
    private EvalCaseMapper evalCaseMapper;

    @AfterEach
    void tearDown() {
        clearInvocations(this.evalRecordService, this.evalCaseMapper);
    }

    @Test
    @DisplayName("插入评估任务成功")
    void shouldOkWhenCreateEvalTask() {
        doNothing().when(this.evalCaseMapper).create(any());
        doNothing().when(this.evalRecordService).createEvalRecord(any());

        EvalCaseEntity entity = new EvalCaseEntity();
        entity.setLatency(100);
        entity.setOutcome(true);
        entity.setInstanceId(1L);

        EvalRecordEntity result1 = new EvalRecordEntity();
        EvalRecordEntity result2 = new EvalRecordEntity();

        this.evalCaseService.createEvalCase(entity, Arrays.asList(result1, result2));
        verify(this.evalCaseMapper, times(1)).create((any()));
        verify(this.evalRecordService, times(1)).createEvalRecord((any()));
    }
}