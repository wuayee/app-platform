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
import com.huawei.jade.app.engine.task.entity.EvalTaskEntity;
import com.huawei.jade.app.engine.task.mapper.EvalTaskMapper;
import com.huawei.jade.app.engine.task.service.EvalTaskService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link EvalTaskServiceImpl} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-09
 */
@FitTestWithJunit(includeClasses = EvalTaskServiceImpl.class)
public class EvalTaskServiceImplTest {
    @Fit
    private EvalTaskService evalTaskService;

    @Mock
    private EvalTaskMapper evalTaskMapper;

    @AfterEach
    void teardown() {
        clearInvocations(this.evalTaskMapper);
    }

    @Test
    @DisplayName("插入评估任务成功")
    void shouldOkWhenCreateEvalTask() {
        doNothing().when(this.evalTaskMapper).create(any());
        EvalTaskEntity entity = new EvalTaskEntity();
        entity.setName("task1");
        entity.setDescription("eval task");
        entity.setStatus("published");
        entity.setAppId("123456");
        entity.setWorkflowId("flow1");

        this.evalTaskService.createEvalTask(entity);
        verify(this.evalTaskMapper, times(1)).create((any()));
    }
}
