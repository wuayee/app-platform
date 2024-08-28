/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.filter.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.util.TypeUtils;
import com.huawei.jade.authentication.AuthenticationService;
import com.huawei.jade.common.code.CommonRetCode;
import com.huawei.jade.common.filter.HttpResult;
import com.huawei.jade.common.filter.config.DefaultHttpResponseWrapperConfig;
import com.huawei.jade.common.test.TestController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Type;

/**
 * 表示 {@link LoginFilter} 的测试套。
 *
 * @author 陈潇文
 * @since 2024-08-07
 */
@MvcTest(classes = {
        TestController.class, DefaultHttpResponseWrapperConfig.class, LoginFilter.class,
        DefaultHttpResponseWrapper.class
})
@DisplayName("测试 LoginFilter")
public class LoginFilterTest {
    @Fit
    private MockMvc mockMvc;

    @Mock
    private AuthenticationService authenticationServiceMock;

    private final String userName = "Jade";

    @BeforeEach
    void setUp() {
        Mockito.when(this.authenticationServiceMock.getUserName(any())).thenReturn(this.userName);
    }

    @Test
    @DisplayName("测试拦截可以获取用户上下文")
    public void shouldOkWhenGetUserContext() {
        String url = "/support/testLoginFilter";
        HttpClassicClientResponse<HttpResult<String>> response = this.mockMvc.perform(MockMvcRequestBuilders.get(url)
                .responseType(TypeUtils.parameterized(HttpResult.class, new Type[] {String.class})));
        assertThat(response.objectEntity().get().object()).hasFieldOrPropertyWithValue("code",
                        CommonRetCode.SUCCESS.getCode())
                .hasFieldOrPropertyWithValue("msg", CommonRetCode.SUCCESS.getMsg())
                .hasFieldOrPropertyWithValue("data", this.userName);
    }
}
