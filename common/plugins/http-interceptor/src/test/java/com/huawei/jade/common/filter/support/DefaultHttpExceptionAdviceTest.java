/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.filter.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.MvcTest;
import com.huawei.fitframework.test.domain.mvc.MockMvc;
import com.huawei.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import com.huawei.fitframework.util.TypeUtils;
import com.huawei.jade.common.filter.HttpResult;
import com.huawei.jade.common.test.TestController;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;

/**
 * 表示 {@link DefaultHttpExceptionAdvice} 的测试套。
 *
 * @author 易文渊
 * @since 2024-07-18
 */
@MvcTest(classes = {TestController.class, DefaultHttpExceptionAdvice.class})
public class DefaultHttpExceptionAdviceTest {
    @Fit
    private MockMvc mockMvc;

    @Test
    @DisplayName("测试拦截 FitException")
    public void shouldOkWhenInterceptException() {
        String url = "/nonsupport/exception";
        HttpClassicClientResponse<?> response = this.mockMvc.perform(MockMvcRequestBuilders.get(url)
                .responseType(TypeUtils.parameterized(HttpResult.class, new Type[] {Void.class})));
        assertThat(response.objectEntity()).isNotEmpty()
                .get()
                .satisfies(objectEntity -> assertThat(objectEntity.object()).hasFieldOrPropertyWithValue("code", 404)
                        .hasFieldOrPropertyWithValue("msg", "test error")
                        .hasFieldOrPropertyWithValue("data", null));
    }
}