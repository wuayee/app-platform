/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.plugin.huggingface.controller;

import static modelengine.fel.plugin.huggingface.code.HuggingfacePluginRetCode.MODEL_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import modelengine.fel.plugin.huggingface.dto.HuggingfaceModelQueryParam;
import modelengine.fel.plugin.huggingface.dto.HuggingfaceTaskInfo;
import modelengine.fel.plugin.huggingface.entity.HuggingfaceModelEntity;
import modelengine.fel.plugin.huggingface.service.HuggingfaceModelQueryService;
import modelengine.fel.plugin.huggingface.service.HuggingfaceTaskQueryService;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.test.domain.mvc.request.MockRequestBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.TypeUtils;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.common.vo.PageVo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link HuggingfaceQueryController} 的测试用例。
 *
 * @author 邱晓霞
 * @since 2024-09-10
 */
@MvcTest(classes = {HuggingfaceQueryController.class})
@DisplayName("测试 HuggingfaceQueryController")
public class HuggingfaceQueryControllerTest {
    @Fit
    private MockMvc mockMvc;

    @Mock
    private HuggingfaceTaskQueryService taskQueryService;

    @Mock
    private HuggingfaceModelQueryService modelQueryService;

    private HttpClassicClientResponse<?> response;

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @DisplayName("查询任务列表接口成功")
    void shouldOkWhenQueryTaskData() {
        HuggingfaceTaskInfo taskInfo = new HuggingfaceTaskInfo();
        taskInfo.setTaskId(1L);
        taskInfo.setTaskName("automatic-speech-recognition");
        taskInfo.setTaskDescription("Automatic Speech Recognition (ASR), also known as Speech to Text (STT), "
                + "is the task of transcribing a given audio to text. "
                + "It has many applications, such as voice user interfaces.");
        List<HuggingfaceTaskInfo> taskInfos = Collections.singletonList(taskInfo);
        Mockito.when(this.taskQueryService.listAvailableTasks()).thenReturn(taskInfos);
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/store/huggingface/tasks")
                .responseType(TypeUtils.parameterized(List.class, new Type[] {HuggingfaceTaskInfo.class}));
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        List<HuggingfaceTaskInfo> target = ObjectUtils.cast(this.response.objectEntity().get().object());
        assertThat(target.get(0).getTaskName()).isEqualTo("automatic-speech-recognition");
    }

    @Test
    @DisplayName("pageIndex 不合法，查询模型数据接口失败")
    void shouldErrWhenPageIndexIllegal() {
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/store/huggingface/models")
                .param("taskId", "1")
                .param("pageIndex", "0")
                .param("pageSize", "1")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {HuggingfaceModelEntity.class}));
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("pageSize 不合法，查询模型数据接口失败")
    void shouldErrWhenPageSizeIllegal() {
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/store/huggingface/models")
                .param("taskId", "1")
                .param("pageIndex", "1")
                .param("pageSize", "0")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {HuggingfaceModelEntity.class}));
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("查询指定任务的默认模型数据成功")
    void shouldOkWhenQueryDefaultModel() {
        HuggingfaceModelEntity modelEntity = new HuggingfaceModelEntity();
        modelEntity.setModelName("name1");
        modelEntity.setModelSchema("testSchema1");
        modelEntity.setTaskId(1L);
        List<HuggingfaceModelEntity> modelEntityList = Collections.singletonList(modelEntity);
        HuggingfaceModelQueryParam queryParam = new HuggingfaceModelQueryParam(1L, 1, 1);
        Mockito.when(this.modelQueryService.listModelInfoQuery(queryParam)).thenReturn(PageVo.of(1, modelEntityList));
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/store/huggingface/tasks/default-model")
                .param("taskId", "1")
                .responseType(HuggingfaceModelEntity.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        assertThat(this.response.objectEntity()).isPresent();
        HuggingfaceModelEntity target = ObjectUtils.cast(this.response.objectEntity().get().object());
        assertThat(target.getModelName()).isEqualTo("name1");
        assertThat(target.getModelSchema()).isEqualTo("testSchema1");
    }

    @Test
    @DisplayName("查询指定任务的默认模型数据接口失败")
    void shouldErrorWhenQueryModel() {
        List<HuggingfaceModelEntity> modelEntityList = new ArrayList<>();
        Mockito.when(this.modelQueryService.listModelInfoQuery(any())).thenReturn(PageVo.of(0, modelEntityList));
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/store/huggingface/tasks/default-model")
                .param("taskId", "1")
                .param("pageIndex", "1")
                .param("pageSize", "1")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {HuggingfaceModelEntity.class}));
        try {
            this.response = this.mockMvc.perform(requestBuilder);
        } catch (ModelEngineException exception) {
            assertThat(exception).isNotNull().hasMessage("Model not found.[taskId=1]");
            assertThat(exception.getCode()).isEqualTo(MODEL_NOT_FOUND.getCode());
        }
    }

    @Test
    @DisplayName("查询指定任务的模型数据接口成功")
    void shouldOkWhenQueryModel() {
        List<HuggingfaceModelEntity> modelEntityList = new ArrayList<>();
        modelEntityList.add(new HuggingfaceModelEntity("name1", "testSchema1", 1L));
        modelEntityList.add(new HuggingfaceModelEntity("name2", "testSchema2", 1L));
        Mockito.when(this.modelQueryService.listModelInfoQuery(any())).thenReturn(PageVo.of(2, modelEntityList));
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/store/huggingface/models")
                .param("taskId", "1")
                .param("pageIndex", "1")
                .param("pageSize", "2")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {HuggingfaceModelEntity.class}));
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        assertThat(this.response.objectEntity()).isPresent();
        PageVo<HuggingfaceModelEntity> target = ObjectUtils.cast(this.response.objectEntity().get().object());
        List<HuggingfaceModelEntity> result = target.getItems();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(1).getModelName()).isEqualTo("name2");
        assertThat(result.get(1).getModelSchema()).isEqualTo("testSchema2");
    }
}