/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.aipp.dto.ToolDto;
import com.huawei.fit.jober.aipp.service.StoreService;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.Mock;
import com.huawei.fitframework.test.annotation.MvcTest;
import com.huawei.fitframework.test.domain.mvc.MockMvc;
import com.huawei.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import com.huawei.fitframework.test.domain.mvc.request.MockRequestBuilder;
import com.huawei.fitframework.util.TypeUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 表示 {@link StoreController} 的测试类。
 *
 * @author 鲁为 l00839724
 * @since 2024-08-02
 */
@MvcTest(classes = {StoreController.class})
@DisplayName("测试 StoreController")
public class StoreControllerTest {
    @Fit
    private MockMvc mockMvc;

    @Mock
    private Authenticator authenticator;

    @Mock
    private StoreService storeService;

    private HttpClassicClientResponse<?> response;

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @DisplayName("当根据参数查询工具时，返回正确结果。")
    void shouldReturnCorrectPluginsWhenGetPlugins() {
        ToolDto toolDto = new ToolDto();
        Mockito.when(this.storeService.getPlugins(any(), any()))
                .thenReturn(toolDto);
        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.get("/v1/api/31f20efc7e0848deab6a6bc10fc3021e/store/plugins")
                        .param("excludeTags", "APP")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .responseType(TypeUtils.parameterized(Rsp.class, new Type[] {ToolDto.class}));
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }
}
