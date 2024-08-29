/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.huawei.jade.app.engine.task.entity.EvalRecordEntity;
import com.huawei.jade.app.engine.task.mapper.EvalRecordMapper;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * 表示 {@link EvalRecordServiceImpl} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@FitTestWithJunit(includeClasses = EvalRecordServiceImpl.class)
public class EvalRecordServiceImplTest {
    @Fit
    private EvalRecordServiceImpl caseResultService;

    @Mock
    private EvalRecordMapper mapper;

    @AfterEach
    void tearDown() {
        clearInvocations(this.mapper);
    }
    @Test
    @DisplayName("插入评估记录成功")
    void shouldOkWhenCreateEvalRecord() {
        doNothing().when(this.mapper).create(anyList());
        EvalRecordEntity result = new EvalRecordEntity();

        this.caseResultService.createEvalRecord(Collections.singletonList(result));
        verify(this.mapper, times(1)).create((any()));
    }
}