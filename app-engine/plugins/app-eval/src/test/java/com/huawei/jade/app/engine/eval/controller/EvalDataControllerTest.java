/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.Mocked;
import com.huawei.fitframework.test.annotation.MvcTest;
import com.huawei.fitframework.test.domain.mvc.MockMvc;
import com.huawei.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import com.huawei.fitframework.test.domain.mvc.request.MockRequestBuilder;
import com.huawei.fitframework.util.TypeUtils;
import com.huawei.jade.app.engine.eval.dto.EvalDataCreateDto;
import com.huawei.jade.app.engine.eval.dto.EvalDataDeleteDto;
import com.huawei.jade.app.engine.eval.dto.EvalDataUpdateDto;
import com.huawei.jade.app.engine.eval.entity.EvalDataEntity;
import com.huawei.jade.app.engine.eval.service.EvalDataService;
import com.huawei.jade.common.vo.PageVo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalDataController} 的测试集。
 *
 * @author 易文渊
 * @since 2024-07-22
 */
@MvcTest(classes = {EvalDataController.class})
@DisplayName("测试 EvalDataController")
public class EvalDataControllerTest {
    @Fit
    private MockMvc mockMvc;

    @Mocked
    private EvalDataService evalDataService;

    @Test
    @DisplayName("批量创建评估数据接口成功")
    void shouldOkWhenCreateEvalData() {
        Mockito.doNothing().when(evalDataService).insertAll(anyLong(), anyList());

        EvalDataCreateDto evalDataCreateDto = new EvalDataCreateDto();
        evalDataCreateDto.setDatasetId(1L);
        evalDataCreateDto.setContents(Collections.singletonList("{}"));

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/data").jsonEntity(evalDataCreateDto).responseType(Void.class);
        HttpClassicClientResponse<Void> response = this.mockMvc.perform(requestBuilder);
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("分页查询评估数据接口成功")
    void shouldOkWhenQueryEvalData() {
        EvalDataEntity entity = new EvalDataEntity();
        entity.setId(1L);
        entity.setContent("abcd");
        List<EvalDataEntity> entities = Collections.singletonList(entity);
        Mockito.when(evalDataService.listEvalData(any())).thenReturn(PageVo.of(1, entities));

        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/eval/data")
                .param("datasetId", "1")
                .param("version", "1")
                .param("pageIndex", "1")
                .param("pageSize", "10")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {EvalDataEntity.class}));

        HttpClassicClientResponse<PageVo<EvalDataEntity>> response = this.mockMvc.perform(requestBuilder);
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.objectEntity()).isPresent();
        assertThat(response.objectEntity().get().object().getTotal()).isEqualTo(1);
        assertThat(response.objectEntity().get().object().getItems()).isNotEmpty()
                .extracting(EvalDataEntity::getContent)
                .contains("abcd");
    }

    @Test
    @DisplayName("修改评估数据接口成功")
    public void shouldOkWhenUpdateEvalData() {
        Mockito.doNothing().when(evalDataService).update(anyLong(), anyLong(), anyString());

        EvalDataUpdateDto evalDataUpdateDto = new EvalDataUpdateDto();
        evalDataUpdateDto.setDatasetId(1L);
        evalDataUpdateDto.setDataId(1L);
        evalDataUpdateDto.setContent("{}");

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.put("/eval/data").jsonEntity(evalDataUpdateDto).responseType(Void.class);
        HttpClassicClientResponse<Void> response = mockMvc.perform(requestBuilder);
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("不合格数据创建评估数据接口失败")
    void shouldFailWhenCreateEvalDataWithInvalidDataId() {
        Mockito.doNothing().when(this.evalDataService).insertAll(anyLong(), anyList());

        EvalDataCreateDto evalDataCreateDto = new EvalDataCreateDto();
        evalDataCreateDto.setDatasetId(0L);
        evalDataCreateDto.setContents(Collections.singletonList("{}"));

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/data").jsonEntity(evalDataCreateDto).responseType(Void.class);
        HttpClassicClientResponse<Void> response = this.mockMvc.perform(requestBuilder);
        assertThat(response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("不合格数据软删除评估数据接口失败")
    void shouldFailWhenDeleteEvalDataWithInvalidDataId() {
        Mockito.doNothing().when(this.evalDataService).delete(anyList());

        EvalDataDeleteDto evalDataDeleteDto = new EvalDataDeleteDto();
        evalDataDeleteDto.setDataIds(Collections.singletonList(-1L));

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.delete("/eval/data").jsonEntity(evalDataDeleteDto).responseType(Void.class);
        HttpClassicClientResponse<Void> response = this.mockMvc.perform(requestBuilder);
        assertThat(response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("批量软删除评估数据接口成功")
    void shouldOkWhenDeleteEvalData() {
        Mockito.doNothing().when(this.evalDataService).delete(anyList());

        EvalDataDeleteDto evalDataDeleteDto = new EvalDataDeleteDto();
        evalDataDeleteDto.setDataIds(Collections.singletonList(1L));

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.delete("/eval/data").jsonEntity(evalDataDeleteDto).responseType(Void.class);
        HttpClassicClientResponse<Void> response = this.mockMvc.perform(requestBuilder);
        assertThat(response.statusCode()).isEqualTo(200);
    }
}