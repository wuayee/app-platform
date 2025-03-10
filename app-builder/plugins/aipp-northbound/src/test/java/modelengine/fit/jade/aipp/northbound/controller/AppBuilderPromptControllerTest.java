/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.northbound.controller;

import static org.assertj.core.api.Assertions.assertThatCode;

import modelengine.fit.jane.task.gateway.Authenticator;

import modelengine.fit.http.protocol.Address;
import modelengine.fit.http.protocol.support.DefaultMessageHeaders;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.support.DefaultCookieCollection;
import modelengine.fit.jober.aipp.genericable.adapter.AppBuilderPromptServiceAdapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * {@link AppBuilderPromptController} 的测试类。
 *
 * @author 曹嘉美
 * @since 2024-12-17
 */
@DisplayName("测试 AppBuilderPromptController")
@ExtendWith(MockitoExtension.class)
class AppBuilderPromptControllerTest {
    private AppBuilderPromptController controller;
    @Mock
    private AppBuilderPromptServiceAdapter appService;
    @Mock
    private Authenticator authenticator;
    @Mock
    private HttpClassicServerRequest request;

    @BeforeEach
    void before() {
        this.controller = new AppBuilderPromptController(authenticator, appService);
    }

    @Test
    @DisplayName("当查询所有灵感类别时，返回正确结果。")
    void shouldReturnOkWhenGetCategories() {
        Mockito.when(request.headers()).thenReturn(new DefaultMessageHeaders());
        Mockito.when(request.cookies()).thenReturn(new DefaultCookieCollection());
        Mockito.when(request.remoteAddress()).thenReturn(Address.builder().hostAddress("127.0.0.1").port(6666).build());
        assertThatCode(() -> this.controller.listCategories(request, "123", "456")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("当查询所有灵感类别时，返回正确结果。")
    void shouldReturnOkWhenGetInspirations() {
        Mockito.when(request.headers()).thenReturn(new DefaultMessageHeaders());
        Mockito.when(request.cookies()).thenReturn(new DefaultCookieCollection());
        Mockito.when(request.remoteAddress()).thenReturn(Address.builder().hostAddress("127.0.0.1").port(6666).build());
        assertThatCode(() -> this.controller.queryInspirations(request,
                "123",
                "456",
                "789")).doesNotThrowAnyException();
    }
}