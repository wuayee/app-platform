/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.IntegrationTest;
import com.huawei.fitframework.test.annotation.Sql;
import com.huawei.fitframework.test.domain.mvc.MockMvc;
import com.huawei.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import com.huawei.fitframework.test.domain.mvc.request.MockRequestBuilder;
import com.huawei.jade.app.engine.eval.dto.EvalDataCreateDto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;

/**
 * 表示评估数据的集成测试用例集。
 *
 * @author 易文渊
 * @since 2024-07-26
 */
@IntegrationTest(scanPackages = "com.huawei.jade.app.engine.eval")
@Sql(scripts = "sql/test_create_table.sql")
@DisplayName("评估数据集成测试")
public class EvalDataIntegrationTest {
    @Fit
    private MockMvc mockMvc;
    private HttpClassicClientResponse<?> response;

    @AfterEach
    void teardown() throws IOException {
        this.response.close();
    }

    @Test
    @Disabled
    @DisplayName("批量创建评估数据接口成功")
    void shouldOkWhenCreateEvalData() throws IOException {
        EvalDataCreateDto evalDataCreateDto = new EvalDataCreateDto();
        evalDataCreateDto.setDatasetId(1L);
        evalDataCreateDto.setContents(Collections.singletonList("{}"));

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/data").jsonEntity(evalDataCreateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }
}