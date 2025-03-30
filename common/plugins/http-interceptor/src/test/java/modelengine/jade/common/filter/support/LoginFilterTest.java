/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.filter.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.util.TypeUtils;
import modelengine.jade.authentication.AuthenticationService;
import modelengine.jade.common.code.CommonRetCode;
import modelengine.jade.common.filter.config.DefaultHttpResponseWrapperConfig;
import modelengine.jade.common.test.TestController;
import modelengine.jade.common.vo.Result;

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
        HttpClassicClientResponse<Result<String>> response = this.mockMvc.perform(MockMvcRequestBuilders.get(url)
                .responseType(TypeUtils.parameterized(Result.class, new Type[] {String.class})));
        assertThat(response.objectEntity().get().object()).hasFieldOrPropertyWithValue("code",
                        CommonRetCode.SUCCESS.getCode())
                .hasFieldOrPropertyWithValue("msg", CommonRetCode.SUCCESS.getMsg())
                .hasFieldOrPropertyWithValue("data", this.userName);
    }
}
