/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.filter.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.http.client.HttpClassicClientResponse;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.util.TypeUtils;
import com.huawei.jade.common.code.CommonRetCode;
import com.huawei.jade.common.filter.HttpResult;
import com.huawei.jade.common.filter.config.DefaultHttpResponseWrapperConfig;
import com.huawei.jade.common.test.TestController;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Type;

/**
 * 表示 {@link DefaultAlbSseFilter} 的测试套。
 *
 * @author 吴宇伦
 * @since 2024-08-01
 */
@MvcTest(classes = {
        TestController.class, DefaultHttpResponseWrapperConfig.class, DefaultAlbSseFilter.class
})
@DisplayName("测试 DefaultHttpResponseWrapper")
public class DefaultAlbSseFilterTest {
    @Fit
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = {"/support/result"})
    @DisplayName("测试拦截正常数据")
    public void shouldOkWhenInterceptData(String url) {
        HttpClassicClientResponse<HttpResult<String>> response = this.mockMvc.perform(MockMvcRequestBuilders.get(url)
                .responseType(TypeUtils.parameterized(HttpResult.class, new Type[] {String.class})));
        assertThat(response.objectEntity().get().object()).hasFieldOrPropertyWithValue("code",
                        CommonRetCode.SUCCESS.getCode())
                .hasFieldOrPropertyWithValue("msg", CommonRetCode.SUCCESS.getMsg())
                .hasFieldOrPropertyWithValue("data", "test");
    }

    @Test
    @DisplayName("测试名称与优先级")
    public void testNameAndPriority() {
        DefaultAlbSseFilter defaultAlbSseFilter = new DefaultAlbSseFilter();
        Assertions.assertEquals(defaultAlbSseFilter.name(), "DefaultAlbSseFilter");
        Assertions.assertEquals(defaultAlbSseFilter.priority(), 1000);
    }

    @Test
    @DisplayName("测试白名单不拦截字符串")
    public void shouldOkWhenNoInterceptString() {
        String url = "/nonsupport/string";
        HttpClassicClientResponse<HttpResult<String>> response =
                this.mockMvc.perform(MockMvcRequestBuilders.get(url).responseType(String.class));
        assertThat(response.textEntity().get().content()).isEqualTo("test");
    }

    @Test
    @DisplayName("测试白名单不拦截错误")
    public void shouldOkWhenNoInterceptResult() {
        String url = "/nonsupport/result";
        HttpClassicClientResponse<HttpResult<String>> response = this.mockMvc.perform(MockMvcRequestBuilders.get(url)
                .responseType(TypeUtils.parameterized(HttpResult.class, new Type[] {Void.class})));
        assertThat(response.objectEntity().get().object()).hasFieldOrPropertyWithValue("code",
                        CommonRetCode.INTERNAL_ERROR.getCode())
                .hasFieldOrPropertyWithValue("msg", CommonRetCode.INTERNAL_ERROR.getMsg())
                .hasFieldOrPropertyWithValue("data", null);
    }
}
