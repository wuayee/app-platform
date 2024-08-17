/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;

import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.Mock;
import com.huawei.fitframework.test.annotation.MvcTest;
import com.huawei.fitframework.test.domain.mvc.MockMvc;
import com.huawei.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import com.huawei.fitframework.test.domain.mvc.request.MockRequestBuilder;
import com.huawei.jade.app.engine.task.dto.EvalInstanceCreateDto;
import com.huawei.jade.app.engine.task.service.EvalInstanceService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * 表示 {@link EvalInstanceController} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-12
 */
@MvcTest(classes = {EvalInstanceController.class})
@DisplayName("测试 EvalTaskInstanceController")
public class EvalInstanceControllerTest {
    @Fit
    private MockMvc mockMvc;

    @Mock
    private EvalInstanceService evalInstanceService;

    private HttpClassicClientResponse<?> response;

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @DisplayName("创建评估任务实例接口成功")
    void shouldOkWhenCreateEvalInstance() {
        doNothing().when(this.evalInstanceService).createEvalInstance(anyLong());

        EvalInstanceCreateDto createDto = new EvalInstanceCreateDto();
        createDto.setTaskId(1L);

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/task/instance").jsonEntity(createDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("创建评估任务实例接口失败")
    void shouldFailWhenCreateEvalInstance() {
        doNothing().when(this.evalInstanceService).createEvalInstance(anyLong());

        EvalInstanceCreateDto createDto = new EvalInstanceCreateDto();
        createDto.setTaskId(0L);

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/task/instance").jsonEntity(createDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }
}
