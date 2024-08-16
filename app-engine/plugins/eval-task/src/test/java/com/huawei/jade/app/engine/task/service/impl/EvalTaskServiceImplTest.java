/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.FitTestWithJunit;
import com.huawei.fitframework.test.annotation.Mock;
import com.huawei.jade.app.engine.task.dto.EvalTaskQueryParam;
import com.huawei.jade.app.engine.task.entity.EvalTaskEntity;
import com.huawei.jade.app.engine.task.mapper.EvalTaskMapper;
import com.huawei.jade.app.engine.task.service.EvalTaskService;
import com.huawei.jade.common.vo.PageVo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

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

    @Test
    @DisplayName("查询全量评估任务元数据成功")
    void shouldOkWhenListEvalTask() {
        Long taskId = 1L;
        String name = "task1";
        String description = "eval task";
        String status = "published";
        String appId = "123456";
        String workflowId = "flow1";

        EvalTaskEntity entity = new EvalTaskEntity();
        entity.setId(taskId);
        entity.setName(name);
        entity.setDescription(description);
        entity.setStatus(status);
        entity.setAppId(appId);
        entity.setWorkflowId(workflowId);
        List<EvalTaskEntity> entities = Collections.singletonList(entity);

        when(this.evalTaskMapper.listEvalTask(any())).thenReturn(entities);
        when(this.evalTaskMapper.countEvalTask(any())).thenReturn(1);

        EvalTaskQueryParam queryParam = new EvalTaskQueryParam();
        PageVo<EvalTaskEntity> response = this.evalTaskService.listEvalTask(queryParam);

        EvalTaskEntity firstEntity = response.getItems().get(0);
        assertThat(response).extracting(PageVo::getTotal, r -> r.getItems().size()).containsExactly(1, 1);
        assertThat(firstEntity).extracting(EvalTaskEntity::getId,
                        EvalTaskEntity::getName,
                        EvalTaskEntity::getDescription,
                        EvalTaskEntity::getStatus,
                        EvalTaskEntity::getAppId,
                        EvalTaskEntity::getWorkflowId)
                .containsExactly(taskId, name, description, status, appId, workflowId);
    }
}
