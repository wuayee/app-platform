/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.northbound.websocket.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.jade.authentication.AuthenticationService;

import modelengine.fit.http.protocol.Address;
import modelengine.fit.http.protocol.support.DefaultMessageHeaders;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.support.DefaultCookieCollection;
import modelengine.fit.http.websocket.Session;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.service.AppChatService;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.jade.authentication.AuthenticationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link AppStreamController} 测试类。
 *
 * @author 方誉州
 * @since 2024-09-12
 */
@ExtendWith(MockitoExtension.class)
public class AppStreamControllerTest {
    private final String chatMsg =
            "{\n" + "    \"method\": \"appChat\",\n" + "    \"params\": {\n" + "        \"tenantId\": \"123\",\n"
                    + "        \"isDebug\": true,\n" + "        \"data\": {\n" + "            \"app_id\": \"app_id\",\n"
                    + "            \"chat_id\": \"chat_id\",\n" + "            \"question\": \"question\",\n"
                    + "            \"context\": {\n" + "                \"use_memory\": true,\n"
                    + "                \"user_context\": null,\n" + "                \"at_app_id\": \"at_app_id\",\n"
                    + "                \"at_chat_id\": \"at_chai_id\",\n" + "                \"dimension\": \"test\"\n"
                    + "            }\n" + "        }\n" + "    }\n" + "}";
    private final List<String> result = new ArrayList<>();
    @Fit
    private AppStreamController appStreamController;
    @Mock
    private Authenticator authenticator;
    @Mock
    private AppChatService appChatService;
    @Mock
    private Session session;
    @Mock
    private HttpClassicServerRequest request;
    @Mock
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setup() {
        ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null);
        this.appStreamController =
                new AppStreamController(this.authenticator, this.appChatService, serializer, authenticationService);
        when(request.headers()).thenReturn(new DefaultMessageHeaders());
        when(request.cookies()).thenReturn(new DefaultCookieCollection());
        when(request.remoteAddress()).thenReturn(Address.builder().hostAddress("127.0.0.1").port(6666).build());
        when(session.getHandshakeMessage()).thenReturn(request);
        when(authenticationService.getUserName(any())).thenReturn("test_user_name");

        result.clear();
        doAnswer(invocationOnMock -> result.add(invocationOnMock.getArgument(0))).when(session).send(anyString());
    }

    @Test
    @DisplayName("测试路由成功")
    void testAppStreamControllerRouter() {
        when(appChatService.chat(any(), any(), anyBoolean())).then(invocationOnMock -> Choir.<Object>just(
                "test route success"));

        appStreamController.onMessage(session, chatMsg);

        assertThat(result).hasSize(2)
                .contains("{\"requestId\":null,\"code\":0,\"msg\":null,\"data\":\"test route success\","
                                + "\"completed\":false}",
                        "{\"requestId\":null,\"code\":0,\"msg\":null,\"data\":null,\"completed\":true}");
    }

    @Test
    @DisplayName("测试接口直接返回 FitException 异常")
    void testInterfaceThrowFitException() {
        when(appChatService.chat(any(), any(), anyBoolean())).thenThrow(new AippException(AippErrCode.UNKNOWN));

        appStreamController.onMessage(session, chatMsg);

        assertThat(result).hasSize(1)
                .contains("{\"requestId\":null,\"code\":90000002,\"msg\":\"服务器内部错误，请联系管理员。\",\"data\":null,"
                        + "\"completed\":true}");
    }

    @Test
    @DisplayName("测试响应流中出现错误")
    void testFailWhenStream() {
        when(appChatService.chat(any(), any(), anyBoolean())).thenReturn(Choir.create(emitter -> {
            emitter.emit("emit success");
            emitter.fail(new IOException("emit fail"));
        }));

        appStreamController.onMessage(session, chatMsg);
        assertThat(result).hasSize(2)
                .contains("{\"requestId\":null,\"code\":0,\"msg\":null,\"data\":\"emit success\",\"completed\":false}",
                        "{\"requestId\":null,\"code\":90000002,\"msg\":\"emit fail\",\"data\":null,"
                                + "\"completed\":true}");
    }
}
