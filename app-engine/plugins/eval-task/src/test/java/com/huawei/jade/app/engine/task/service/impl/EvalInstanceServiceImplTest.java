/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import com.huawei.jade.app.engine.task.mapper.EvalInstanceMapper;
import com.huawei.jade.app.engine.task.service.EvalInstanceService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link EvalInstanceServiceImpl} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-12
 */
@FitTestWithJunit(includeClasses = EvalInstanceServiceImpl.class)
public class EvalInstanceServiceImplTest {
    @Fit
    private EvalInstanceService service;

    @Mock
    private EvalInstanceMapper mapper;

    @AfterEach
    void teardown() {
        clearInvocations(this.mapper);
    }

    @Test
    @DisplayName("插入评估任务实例后，回填主键成功")
    void shouldOkWhenCreateEvalInstance() {
        doNothing().when(this.mapper).create(any());
        this.service.createEvalInstance(1L);
        verify(this.mapper, times(1)).create((any()));
    }
}
