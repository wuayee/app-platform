/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.northbound.controller;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import modelengine.fit.jane.task.gateway.Authenticator;

import modelengine.fit.http.protocol.Address;
import modelengine.fit.http.protocol.support.DefaultMessageHeaders;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.support.DefaultCookieCollection;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.dto.chat.ChatRequest;
import modelengine.fit.jober.aipp.genericable.adapter.AippChatServiceAdapter;
import modelengine.fit.jober.aipp.genericable.adapter.AppChatServiceAdapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

/**
 * {@link AppChatController} 的测试类。
 *
 * @author 曹嘉美
 * @since 2024-12-17
 */
@DisplayName("测试 AppChatController")
@ExtendWith(MockitoExtension.class)
public class AppChatControllerTest {
    private AppChatController controller;
    @Mock
    private Authenticator authenticator;
    @Mock
    private AppChatServiceAdapter appChatService;
    @Mock
    private AippChatServiceAdapter aippChatService;
    @Mock
    private HttpClassicServerRequest request;

    @BeforeEach
    void before() {
        this.controller = new AppChatController(authenticator, appChatService, aippChatService);
    }

    @Test
    @DisplayName("当发送会话消息时，返回正确结果。")
    void shouldReturnOkWhenChat() {
        ChatRequest body = ChatRequest.builder()
                .chatId("appid")
                .context(ChatRequest.Context.builder().build())
                .question("q")
                .build();
        Mockito.when(request.headers()).thenReturn(new DefaultMessageHeaders());
        Mockito.when(request.cookies()).thenReturn(new DefaultCookieCollection());
        Mockito.when(request.remoteAddress()).thenReturn(Address.builder().hostAddress("127.0.0.1").port(6666).build());
        assertThatCode(() -> this.controller.chat(request, "123", "456", body)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("当会话信息缺少问题时，抛出异常。")
    void shouldThrowExceptionWhenChatNoQuestion() {
        ChatRequest body = ChatRequest.builder().chatId("appid").context(ChatRequest.Context.builder().build()).build();
        assertThatThrownBy(() -> this.controller.chat(request,
                "123",
                "456",
                body)).isInstanceOf(AippParamException.class)
                .hasFieldOrPropertyWithValue("code", AippErrCode.APP_CHAT_QUESTION_IS_NULL.getErrorCode());
    }

    @Test
    @DisplayName("当会话请求缺少请求体时，抛出异常")
    void shouldThrowExceptionWhenNoBody() {
        assertThatThrownBy(() -> this.controller.chat(request,
                "123",
                null,
                null)).isInstanceOf(AippParamException.class)
                .hasFieldOrPropertyWithValue("code", AippErrCode.APP_CHAT_REQUEST_IS_NULL.getErrorCode());
    }

    @Test
    @DisplayName("当重开会话时，返回正确结果。")
    void shouldReturnOkWhenChatRestartChat() {
        Mockito.when(request.headers()).thenReturn(new DefaultMessageHeaders());
        Mockito.when(request.cookies()).thenReturn(new DefaultCookieCollection());
        Mockito.when(request.remoteAddress()).thenReturn(Address.builder().hostAddress("127.0.0.1").port(6666).build());
        assertThatCode(() -> this.controller.restartChat(request,
                "123",
                "123",
                new HashMap<>())).doesNotThrowAnyException();
    }
}
