/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.Mock;
import com.huawei.fitframework.test.annotation.MvcTest;
import com.huawei.fitframework.test.domain.mvc.MockMvc;
import com.huawei.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import com.huawei.fitframework.test.domain.mvc.request.MockRequestBuilder;
import com.huawei.fitframework.transaction.DataAccessException;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.TypeUtils;
import com.huawei.jade.app.engine.eval.dto.EvalDatasetCreateDto;
import com.huawei.jade.app.engine.eval.dto.EvalDatasetDeleteParam;
import com.huawei.jade.app.engine.eval.dto.EvalDatasetUpdateDto;
import com.huawei.jade.app.engine.eval.entity.EvalDatasetEntity;
import com.huawei.jade.app.engine.eval.service.EvalDatasetService;
import com.huawei.jade.common.vo.PageVo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Arrays;
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

    @Test
    @DisplayName("创建评估数据集接口成功")
    void shouldOkWhenCreateEvalDataset() {
        doNothing().when(this.evalDatasetService).create(any());

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
    void shouldNotOkWhenCreateEvalDatasetWithoutApplicationId() {
        doNothing().when(this.evalDatasetService).create(any());

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
    @DisplayName("删除单个评估数据集接口成功")
    void shouldOkWhenDeleteSingleEvalDataset() {
        doNothing().when(this.evalDatasetService).delete(anyList());

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.delete("/eval/dataset").param("datasetIds", "1").responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("非法入参删除单个评估数据集接口失败")
    void shouldFailWhenDeleteSingleEvalDatasetWithInvalidId() {
        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.delete("/eval/dataset").param("datasetIds", "-1").responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("批量删除评估数据集接口成功")
    void shouldOkWhenDeleteEvalDataset() {
        doNothing().when(this.evalDatasetService).delete(anyList());

        EvalDatasetDeleteParam evalDatasetDeleteParam = new EvalDatasetDeleteParam();
        evalDatasetDeleteParam.setDatasetIds(Arrays.asList(1L, 2L));

        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/eval/dataset")
                .param("datasetIds", Arrays.asList("1", "2"))
                .responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("删除评估数据集接口失败")
    void shouldFailWhenDeleteEvalDataset() {
        doThrow(new DataAccessException("Fail message")).when(this.evalDatasetService).delete(anyList());

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.delete("/eval/dataset").param("datasetIds", "1").responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("非法入参批量删除评估数据集接口失败")
    void shouldFailWhenDeleteEvalDatasetWithInvalidInput() {
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/eval/dataset")
                .param("datasetIds", Arrays.asList("-1", "2"))
                .responseType(Void.class);
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

        when(this.evalDatasetService.listEvalDataset(any())).thenReturn(PageVo.of(1, entities));
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

        when(this.evalDatasetService.getEvalDatasetById(any())).thenReturn(entity);
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
    @DisplayName("参数错误导致根据 ID 查询评估数据集接口失败")
    void shouldNotOkWhenQueryEvalDatasetById() {
        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.get("/eval/dataset/-1").responseType(EvalDatasetEntity.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("修改数据集信息接口成功")
    void shouldOkWhenUpdateDataset() {
        doNothing().when(this.evalDatasetService).updateEvalDataset(any());
        EvalDatasetUpdateDto updateDto = new EvalDatasetUpdateDto();
        updateDto.setId(1L);
        updateDto.setName("name1");
        updateDto.setDescription("desc1");

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.put("/eval/dataset").jsonEntity(updateDto).responseType(Void.class);

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("修改数据集信息接口成功")
    void shouldOkWhenUpdateDataset1() {
        doNothing().when(this.evalDatasetService).updateEvalDataset(any());
        EvalDatasetUpdateDto updateDto = new EvalDatasetUpdateDto();
        updateDto.setId(1L);
        updateDto.setName("name1");
        updateDto.setDescription(null);

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.put("/eval/dataset").jsonEntity(updateDto).responseType(Void.class);

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("缺少参数导致修改数据集信息接口失败")
    void shouldNotOkWhenUpdateDataset() {
        EvalDatasetUpdateDto updateDto = new EvalDatasetUpdateDto();
        updateDto.setName("name1");
        updateDto.setDescription("desc1");
        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.put("/eval/dataset").jsonEntity(updateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }
}
