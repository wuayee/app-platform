/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;

import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.Mocked;
import com.huawei.fitframework.test.annotation.MvcTest;
import com.huawei.fitframework.test.domain.mvc.MockMvc;
import com.huawei.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import com.huawei.fitframework.test.domain.mvc.request.MockRequestBuilder;
import com.huawei.jade.app.engine.eval.dto.EvalDataCreateDto;
import com.huawei.jade.app.engine.eval.service.EvalDataService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

/**
 * 表示 {@link EvalDataController} 的测试集。
 *
 * @author 易文渊
 * @since 2024-07-22
 */
@MvcTest(classes = EvalDataController.class)
@DisplayName("测试 EvalDataController")
public class EvalDataControllerTest {
    @Fit
    private MockMvc mockMvc;

    @Mocked
    private EvalDataService evalDataService;

    @Test
    @DisplayName("批量创建评估数据接口成功")
    public void shouldOkWhenCreateEvalData() {
        Mockito.doNothing().when(evalDataService).insertAll(anyLong(), anyList());

        EvalDataCreateDto evalDataCreateDto = new EvalDataCreateDto();
        evalDataCreateDto.setDatasetId(1L);
        evalDataCreateDto.setContents(Collections.singletonList("{}"));

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/data").jsonEntity(evalDataCreateDto).responseType(Void.class);
        HttpClassicClientResponse<Void> response = mockMvc.perform(requestBuilder);
        assertThat(response.statusCode()).isEqualTo(200);
    }
}