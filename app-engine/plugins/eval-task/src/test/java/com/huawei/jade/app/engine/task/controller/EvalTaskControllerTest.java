/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.controller;

import static com.huawei.jade.app.engine.task.entity.EvalTaskStatusEnum.PUBLISHED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.test.domain.mvc.request.MockRequestBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.TypeUtils;
import com.huawei.jade.app.engine.task.dto.EvalTaskCreateDto;
import com.huawei.jade.app.engine.task.entity.EvalTaskEntity;
import com.huawei.jade.app.engine.task.entity.EvalTaskStatusEnum;
import com.huawei.jade.app.engine.task.service.EvalTaskService;
import com.huawei.jade.common.vo.PageVo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalTaskController} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-09
 */
@MvcTest(classes = {EvalTaskController.class})
@DisplayName("测试 EvalTaskController")
public class EvalTaskControllerTest {
    @Fit
    private MockMvc mockMvc;

    @Mock
    private EvalTaskService evalTaskService;

    private HttpClassicClientResponse<?> response;

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @DisplayName("创建评估任务接口成功")
    void shouldOkWhenCreateEvalTask() {
        doNothing().when(this.evalTaskService).createEvalTask(any());

        EvalTaskCreateDto evalTaskCreateDto = new EvalTaskCreateDto();
        evalTaskCreateDto.setName("task1");
        evalTaskCreateDto.setDescription("eval task");
        evalTaskCreateDto.setStatus(PUBLISHED);
        evalTaskCreateDto.setAppId("123456");
        evalTaskCreateDto.setWorkflowId("flow1");

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/task").jsonEntity(evalTaskCreateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("创建评估任务接口失败")
    void shouldFailWhenCreateEvalTask() {
        doNothing().when(this.evalTaskService).createEvalTask(any());

        EvalTaskCreateDto evalTaskCreateDto = new EvalTaskCreateDto();

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/task").jsonEntity(evalTaskCreateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("分页查询评估任务接口成功")
    void shouldOkWhenQueryEvalTask() {
        Long taskId = 1L;
        String name = "task1";
        String description = "eval task";
        EvalTaskStatusEnum status = PUBLISHED;
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
        when(this.evalTaskService.listEvalTask(any())).thenReturn(PageVo.of(1, entities));

        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/eval/task")
                .param("appId", "123456")
                .param("pageIndex", "1")
                .param("pageSize", "10")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {EvalTaskEntity.class}));

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        assertThat(this.response.objectEntity()).isPresent();

        PageVo<EvalTaskEntity> target = ObjectUtils.cast(this.response.objectEntity().get().object());
        assertThat(target.getTotal()).isEqualTo(1);
        assertThat(target.getItems()).isNotEmpty();
        assertThat(target.getItems().get(0)).extracting(EvalTaskEntity::getId,
                        EvalTaskEntity::getName,
                        EvalTaskEntity::getDescription,
                        EvalTaskEntity::getStatus,
                        EvalTaskEntity::getAppId,
                        EvalTaskEntity::getWorkflowId)
                .containsExactly(taskId, name, description, status, appId, workflowId);
    }
}
