/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.Mock;
import com.huawei.fitframework.test.annotation.MvcTest;
import com.huawei.fitframework.test.domain.mvc.MockMvc;
import com.huawei.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import com.huawei.fitframework.test.domain.mvc.request.MockRequestBuilder;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.TypeUtils;
import com.huawei.jade.app.engine.eval.dto.EvalDatasetCreateDto;
import com.huawei.jade.app.engine.eval.entity.EvalDatasetEntity;
import com.huawei.jade.app.engine.eval.service.EvalDatasetService;
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
 * 表示 {@link EvalDataController} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-07-31
 */
@MvcTest(classes = {EvalDatasetController.class})
@DisplayName("测试 EvalDatasetController")
public class EvalDatasetControllerTest {
    @Fit
    private MockMvc mockMvc;

    @Mock
    private EvalDatasetService evalDatasetService;

    private HttpClassicClientResponse<?> response;

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @DisplayName("创建评估数据集接口成功")
    void shouldOkWhenCreateEvalData() {
        Mockito.doNothing().when(this.evalDatasetService).create(any());

        EvalDatasetCreateDto evalDatasetCreateDto = new EvalDatasetCreateDto();
        evalDatasetCreateDto.setName("ds1");
        evalDatasetCreateDto.setDescription("Test dataset");
        evalDatasetCreateDto.setContents(Collections.singletonList("{}"));
        evalDatasetCreateDto.setSchema("{}");
        evalDatasetCreateDto.setAppId("1");

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/dataset").jsonEntity(evalDatasetCreateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("创建评估数据集接口失败")
    void shouldNotOkWhenCreateEvalDataWithoutApplicationId() {
        Mockito.doNothing().when(this.evalDatasetService).create(any());

        EvalDatasetCreateDto evalDatasetCreateDto = new EvalDatasetCreateDto();
        evalDatasetCreateDto.setName("ds1");
        evalDatasetCreateDto.setDescription("Test dataset");
        evalDatasetCreateDto.setContents(Collections.singletonList("{}"));
        evalDatasetCreateDto.setSchema("{}");

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/dataset").jsonEntity(evalDatasetCreateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("分页查询评估数据集接口成功")
    void shouldOkWhenQueryEvalDataset() {
        long datasetId = 1L;
        String datasetName = "TEST DATASET";
        String description = "A DATASET FOR TEST";
        String schema = "{FAKE SCHEMA}";

        EvalDatasetEntity entity = new EvalDatasetEntity();
        entity.setId(datasetId);
        entity.setName(datasetName);
        entity.setDescription(description);
        entity.setSchema(schema);
        List<EvalDatasetEntity> entities = Collections.singletonList(entity);

        Mockito.when(this.evalDatasetService.listEvalDataset(any())).thenReturn(PageVo.of(1, entities));
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/eval/dataset")
                .param("appId", "1")
                .param("pageIndex", "1")
                .param("pageSize", "10")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {EvalDatasetEntity.class}));

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        assertThat(this.response.objectEntity()).isPresent();

        PageVo<EvalDatasetEntity> target = ObjectUtils.cast(this.response.objectEntity().get().object());
        assertThat(target.getTotal()).isEqualTo(1);
        assertThat(target.getItems()).isNotEmpty();
        assertThat(target.getItems().get(0)).extracting(EvalDatasetEntity::getId,
                EvalDatasetEntity::getName,
                EvalDatasetEntity::getDescription,
                EvalDatasetEntity::getSchema).containsExactly(datasetId, datasetName, description, schema);
    }

    @Test
    @DisplayName("缺少参数导致分页查询评估数据集接口失败")
    void shouldNotOkWhenQueryEvalDataset() {
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/eval/dataset")
                .param("pageIndex", "1")
                .param("pageSize", "10")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {EvalDatasetEntity.class}));

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("根据 ID 查询评估数据集接口成功")
    void shouldOkWhenQueryEvalDatasetById() {
        long datasetId = 1L;
        String datasetName = "TEST DATASET";
        String description = "A DATASET FOR TEST";
        String schema = "{FAKE SCHEMA}";

        EvalDatasetEntity entity = new EvalDatasetEntity();
        entity.setId(datasetId);
        entity.setName(datasetName);
        entity.setDescription(description);
        entity.setSchema(schema);

        Mockito.when(this.evalDatasetService.getEvalDatasetById(any())).thenReturn(entity);
        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.get("/eval/dataset/1").responseType(EvalDatasetEntity.class);

        this.response = this.mockMvc.perform(requestBuilder);

        assertThat(this.response.statusCode()).isEqualTo(200);
        assertThat(this.response.objectEntity()).isPresent();

        EvalDatasetEntity target = ObjectUtils.cast(this.response.objectEntity().get().object());
        assertThat(target).extracting(EvalDatasetEntity::getSchema,
                EvalDatasetEntity::getName,
                EvalDatasetEntity::getDescription).containsExactly(schema, datasetName, description);
    }

    @Test
    @DisplayName("参数错误导致根据 ID 查询评估数据集接口成功")
    void shouldNotOkWhenQueryEvalDatasetById() {
        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.get("/eval/dataset/-1").responseType(EvalDatasetEntity.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }
}
