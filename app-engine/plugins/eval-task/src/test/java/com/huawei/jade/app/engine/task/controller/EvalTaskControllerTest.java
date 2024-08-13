/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.Mock;
import com.huawei.fitframework.test.annotation.MvcTest;
import com.huawei.fitframework.test.domain.mvc.MockMvc;
import com.huawei.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import com.huawei.fitframework.test.domain.mvc.request.MockRequestBuilder;
import com.huawei.jade.app.engine.task.dto.EvalTaskCreateDto;
import com.huawei.jade.app.engine.task.service.EvalTaskService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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
        evalTaskCreateDto.setStatus("published");
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
}
