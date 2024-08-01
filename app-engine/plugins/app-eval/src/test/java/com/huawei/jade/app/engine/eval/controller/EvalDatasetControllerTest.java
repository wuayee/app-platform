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
import com.huawei.jade.app.engine.eval.dto.EvalDatasetCreateDto;
import com.huawei.jade.app.engine.eval.service.EvalDatasetService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Collections;

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
        Mockito.doNothing()
                .when(this.evalDatasetService)
                .create(any());

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
        Mockito.doNothing()
                .when(this.evalDatasetService)
                .create(any());

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
}
