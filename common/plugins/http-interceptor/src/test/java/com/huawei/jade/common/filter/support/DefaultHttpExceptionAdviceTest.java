/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.filter.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.http.client.HttpClassicClientResponse;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.util.TypeUtils;
import com.huawei.jade.common.filter.HttpResult;
import com.huawei.jade.common.localemessage.LocaleMessageHandler;
import com.huawei.jade.common.test.TestController;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
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

    @Mock
    private LocaleMessageHandler messageHandler;

    private HttpClassicClientResponse<?> response;

    @AfterEach
    void tearDown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @DisplayName("测试拦截 FitException")
    public void shouldOkWhenInterceptFitException() {
        Mockito.when(messageHandler.getLocaleMessage(Mockito.any(), Mockito.any(), Mockito.any()))
            .thenReturn("test error");
        String url = "/nonsupport/exception";
        this.response = this.mockMvc.perform(MockMvcRequestBuilders.get(url)
                .responseType(TypeUtils.parameterized(HttpResult.class, new Type[] {Void.class})));
        assertThat(this.response.objectEntity()).isNotEmpty()
                .get()
                .satisfies(objectEntity -> assertThat(objectEntity.object()).hasFieldOrPropertyWithValue("code", 404)
                        .hasFieldOrPropertyWithValue("msg", "test error")
                        .hasFieldOrPropertyWithValue("data", null));
    }

    @Test
    @DisplayName("测试拦截 Exception")
    void shouldOkWhenInterceptException() {
        String systemDefaultMessage = "system default message";
        Mockito.when(messageHandler.getDefaultMessage()).thenReturn(systemDefaultMessage);
        DefaultHttpExceptionAdvice mockedDefaultHttpExceptionAdvice = new DefaultHttpExceptionAdvice(messageHandler);
        HttpResult<Void> voidHttpResult = mockedDefaultHttpExceptionAdvice.handleException(new Throwable());
        assertThat(voidHttpResult.getMsg()).isEqualTo(systemDefaultMessage);
    }
}