/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.controller;

import static com.huawei.jade.app.engine.task.entity.EvalInstanceStatusEnum.RUNNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;

import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.test.domain.mvc.request.MockRequestBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.TypeUtils;

import com.huawei.jade.app.engine.task.dto.EvalInstanceCreateDto;
import com.huawei.jade.app.engine.task.entity.EvalInstanceEntity;
import com.huawei.jade.app.engine.task.service.EvalInstanceService;
import com.huawei.jade.common.vo.PageVo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

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

    @Test
    @DisplayName("分页查询评估任务实例接口成功")
    void shouldSuccessWhenQueryEvalInstance() {
        EvalInstanceEntity entity = new EvalInstanceEntity();
        entity.setId(1L);
        entity.setPassRate(76.0);
        entity.setCreatedBy("Sky");
        entity.setStatus(RUNNING);
        entity.setTaskId(1L);
        List<EvalInstanceEntity> entities = Collections.singletonList(entity);
        Mockito.when(this.evalInstanceService.listEvalInstance(any())).thenReturn(PageVo.of(1, entities));

        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/eval/task/instance")
                .param("datasetId", "1")
                .param("version", "1")
                .param("taskId", "1")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {EvalInstanceEntity.class}));

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        assertThat(this.response.objectEntity()).isPresent();
        PageVo<EvalInstanceEntity> target = ObjectUtils.cast(this.response.objectEntity().get().object());
        assertThat(target.getTotal()).isEqualTo(1);
        assertThat(target.getItems().get(0)).extracting(EvalInstanceEntity::getPassRate,
                EvalInstanceEntity::getCreatedBy,
                EvalInstanceEntity::getStatus,
                EvalInstanceEntity::getTaskId).containsExactly(76.0, "Sky", RUNNING, 1L);
    }
}
