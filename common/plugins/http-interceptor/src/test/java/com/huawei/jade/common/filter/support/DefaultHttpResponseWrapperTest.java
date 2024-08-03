/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.filter.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.http.entity.TextEntity;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.MvcTest;
import com.huawei.fitframework.test.domain.mvc.MockMvc;
import com.huawei.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.TypeUtils;
import com.huawei.jade.common.code.CommonRetCode;
import com.huawei.jade.common.filter.HttpResult;
import com.huawei.jade.common.filter.config.DefaultHttpResponseWrapperConfig;
import com.huawei.jade.common.test.TestController;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * 表示 {@link DefaultHttpResponseWrapper} 的测试套。
 *
 * @author 易文渊
 * @since 2024-07-18
 */
@MvcTest(classes = {
        TestController.class, DefaultHttpResponseWrapperConfig.class, DefaultHttpResponseWrapper.class
})
@DisplayName("测试 DefaultHttpResponseWrapper")
public class DefaultHttpResponseWrapperTest {
    @Fit
    private MockMvc mockMvc;

    private HttpClassicClientResponse<?> response;

    @AfterEach
    void tearDown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"/support/string", "/support/result"})
    @DisplayName("测试拦截正常数据")
    public void shouldOkWhenInterceptData(String url) {
        this.response = this.mockMvc.perform(MockMvcRequestBuilders.get(url)
                .responseType(TypeUtils.parameterized(HttpResult.class, new Type[] {String.class})));
        assertThat(this.response.objectEntity()).hasValueSatisfying(
                objectEntity -> assertThat(objectEntity.object())
                        .hasFieldOrPropertyWithValue("code", CommonRetCode.SUCCESS.getCode())
                        .hasFieldOrPropertyWithValue("msg", CommonRetCode.SUCCESS.getMsg())
                        .hasFieldOrPropertyWithValue("data", "test")
        );
    }

    @Test
    @DisplayName("测试拦截 void")
    public void shouldOkWhenInterceptVoid() {
        String url = "/support/void";
        this.response = this.mockMvc.perform(MockMvcRequestBuilders.get(url)
                .responseType(TypeUtils.parameterized(HttpResult.class, new Type[] {Void.class})));
        assertThat(this.response.objectEntity()).hasValueSatisfying(
                objectEntity -> assertThat(objectEntity.object())
                        .hasFieldOrPropertyWithValue("code", CommonRetCode.SUCCESS.getCode())
                        .hasFieldOrPropertyWithValue("msg", CommonRetCode.SUCCESS.getMsg())
                        .hasFieldOrPropertyWithValue("data", null)
        );
    }

    @Test
    @DisplayName("测试拦截数值类型")
    public void shouldOkWhenInterceptInteger() {
        String url = "/support/int";
        this.response = this.mockMvc.perform(MockMvcRequestBuilders.get(url)
                .responseType(TypeUtils.parameterized(HttpResult.class, new Type[] {Integer.class})));
        assertThat(this.response.objectEntity()).hasValueSatisfying(
                objectEntity -> assertThat(objectEntity.object())
                        .hasFieldOrPropertyWithValue("code", CommonRetCode.SUCCESS.getCode())
                        .hasFieldOrPropertyWithValue("msg", CommonRetCode.SUCCESS.getMsg())
                        .hasFieldOrPropertyWithValue("data", 0)
        );
    }

    @Test
    @DisplayName("测试白名单不拦截字符串")
    public void shouldOkWhenNoInterceptString() {
        String url = "/nonsupport/string";
        this.response = this.mockMvc.perform(MockMvcRequestBuilders.get(url).responseType(String.class));
        assertThat(response.textEntity()).map(TextEntity::content).hasValue("test");
    }

    @Test
    @DisplayName("测试白名单不拦截错误")
    public void shouldOkWhenNoInterceptResult() {
        String url = "/nonsupport/result";
        this.response = this.mockMvc.perform(MockMvcRequestBuilders.get(url)
                .responseType(TypeUtils.parameterized(HttpResult.class, new Type[] {Void.class})));
        assertThat(this.response.objectEntity()).hasValueSatisfying(
                objectEntity -> assertThat(objectEntity.object())
                        .hasFieldOrPropertyWithValue("code", CommonRetCode.INTERNAL_ERROR.getCode())
                        .hasFieldOrPropertyWithValue("msg", CommonRetCode.INTERNAL_ERROR.getMsg())
                        .hasFieldOrPropertyWithValue("data", null)
        );
    }

    @Test
    @DisplayName("测试流式请求不拦截")
    public void shouldOkWhenNoInterceptStream() {
        String url = "/support/stream";
        this.response = this.mockMvc.perform(MockMvcRequestBuilders.get(url).responseType(Integer.class));
        assertThat(this.response.textEventStreamEntity()).map(entity -> entity.stream()
                .map(textEvent -> ObjectUtils.<Integer>cast(textEvent.data()))
                .reduce(Integer::sum)
                .block()).hasValue(Optional.of(6));
    }
}