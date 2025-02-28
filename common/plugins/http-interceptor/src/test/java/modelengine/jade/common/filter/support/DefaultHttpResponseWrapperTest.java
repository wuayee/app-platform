/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.filter.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.entity.TextEntity;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.flowable.FlowableException;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.util.TypeUtils;
import modelengine.jade.common.code.CommonRetCode;
import modelengine.jade.common.filter.config.DefaultHttpResponseWrapperConfig;
import modelengine.jade.common.test.TestController;
import modelengine.jade.common.vo.Result;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.lang.reflect.Type;

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
                .responseType(TypeUtils.parameterized(Result.class, new Type[] {String.class})));
        assertThat(this.response.objectEntity()).isNotEmpty()
                .get()
                .satisfies(objectEntity -> assertThat(objectEntity.object()).hasFieldOrPropertyWithValue("code",
                                CommonRetCode.SUCCESS.getCode())
                        .hasFieldOrPropertyWithValue("msg", CommonRetCode.SUCCESS.getMsg())
                        .hasFieldOrPropertyWithValue("data", "test"));
    }

    @Test
    @DisplayName("测试拦截 void")
    public void shouldOkWhenInterceptVoid() {
        String url = "/support/void";
        this.response = this.mockMvc.perform(MockMvcRequestBuilders.get(url)
                .responseType(TypeUtils.parameterized(Result.class, new Type[] {Void.class})));
        assertThat(this.response.objectEntity()).isNotEmpty()
                .get()
                .satisfies(objectEntity -> assertThat(objectEntity.object()).hasFieldOrPropertyWithValue("code",
                                CommonRetCode.SUCCESS.getCode())
                        .hasFieldOrPropertyWithValue("msg", CommonRetCode.SUCCESS.getMsg())
                        .hasFieldOrPropertyWithValue("data", null));
    }

    @Test
    @DisplayName("测试拦截数值类型")
    public void shouldOkWhenInterceptInteger() {
        String url = "/support/int";
        this.response = this.mockMvc.perform(MockMvcRequestBuilders.get(url)
                .responseType(TypeUtils.parameterized(Result.class, new Type[] {Integer.class})));
        assertThat(this.response.objectEntity()).isNotEmpty()
                .get()
                .satisfies(objectEntity -> assertThat(objectEntity.object()).hasFieldOrPropertyWithValue("code",
                                CommonRetCode.SUCCESS.getCode())
                        .hasFieldOrPropertyWithValue("msg", CommonRetCode.SUCCESS.getMsg())
                        .hasFieldOrPropertyWithValue("data", 0));
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
                .responseType(TypeUtils.parameterized(Result.class, new Type[] {Void.class})));
        assertThat(this.response.objectEntity()).isNotEmpty()
                .get()
                .satisfies(objectEntity -> assertThat(objectEntity.object()).hasFieldOrPropertyWithValue("code",
                                CommonRetCode.INTERNAL_ERROR.getCode())
                        .hasFieldOrPropertyWithValue("msg", CommonRetCode.INTERNAL_ERROR.getMsg())
                        .hasFieldOrPropertyWithValue("data", null));
    }

    @Test
    @DisplayName("测试流式请求不拦截")
    public void shouldOkWhenNoInterceptStream() {
        String url = "/support/stream";
        Choir<Integer> choir = this.mockMvc.streamPerform(MockMvcRequestBuilders.get(url).responseType(Integer.class));
        assertThat(choir.reduce(Integer::sum).block()).hasValue(6);
    }

    @Test
    @DisplayName("测试流式服务端异常情况下，客户端获取结果出现异常")
    public void shouldExWhenStreamException() {
        String url = "/support/stream/ex";
        Choir<Integer> choir = this.mockMvc.streamPerform(MockMvcRequestBuilders.get(url).responseType(Integer.class));
        assertThatThrownBy(() -> choir.reduce(Integer::sum).block()).isInstanceOf(FlowableException.class);
    }
}