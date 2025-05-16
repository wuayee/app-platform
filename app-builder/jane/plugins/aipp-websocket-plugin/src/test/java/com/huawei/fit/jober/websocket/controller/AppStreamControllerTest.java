/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.websocket.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.service.AppWsCommand;
import modelengine.fit.jober.aipp.service.AppWsRegistryService;
import modelengine.jade.authentication.AuthenticationService;

import modelengine.fit.http.protocol.Address;
import modelengine.fit.http.protocol.support.DefaultMessageHeaders;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.support.DefaultCookieCollection;
import modelengine.fit.http.websocket.Session;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
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
    @Mock
    private Session session;
    @Mock
    private HttpClassicServerRequest request;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private Authenticator authenticator;
    @Mock
    private AppWsRegistryService registry;
    @Mock
    private AppWsCommand command;
    private ObjectSerializer serializer;
    private AppStreamController appStreamController;

    @BeforeEach
    public void setup() {
        this.serializer = new JacksonObjectSerializer(null, null, null);
        this.appStreamController =
                new AppStreamController(this.authenticator, serializer, authenticationService, registry);
        when(request.headers()).thenReturn(new DefaultMessageHeaders());
        when(request.cookies()).thenReturn(new DefaultCookieCollection());
        when(request.remoteAddress()).thenReturn(Address.builder().hostAddress("127.0.0.1").port(6666).build());
        when(session.getHandshakeMessage()).thenReturn(request);
        when(authenticationService.getUserName(any())).thenReturn("test_user_name");
        when(registry.getCommand(anyString())).thenReturn(command);
        result.clear();
        doAnswer(invocationOnMock -> result.add(invocationOnMock.getArgument(0))).when(session).send(anyString());
    }

    @Test
    @DisplayName("测试路由成功")
    void testAppStreamControllerRouter() {
        when(command.execute(any(), any())).then(invocationOnMock -> Choir.<Object>just("test route success"));
        appStreamController.onMessage(session, chatMsg, "123");
        assertThat(result).hasSize(2)
                .contains("{\"requestId\":null,\"code\":0,\"msg\":null,\"data\":\"test route success\","
                                + "\"completed\":false}",
                        "{\"requestId\":null,\"code\":0,\"msg\":null,\"data\":null,\"completed\":true}");
    }

    @Test
    @DisplayName("测试路由非注册接口失败")
    void testRouterUnregister() {
        when(registry.getCommand(anyString())).thenReturn(null);
        String message =
                "{\n" + "    \"method\": \"nonexist\",\n" + "    \"params\": {\n" + "        \"tenantId\": \"123\",\n"
                        + "        \"isDebug\": true,\n" + "        \"data\": {\n"
                        + "            \"app_id\": \"app_id\",\n" + "            \"chat_id\": \"chat_id\",\n"
                        + "            \"question\": \"question\",\n" + "            \"context\": {\n"
                        + "                \"use_memory\": true,\n" + "                \"user_context\": null,\n"
                        + "                \"at_app_id\": \"at_app_id\",\n"
                        + "                \"at_chat_id\": \"at_chai_id\",\n"
                        + "                \"dimension\": \"test\"\n" + "            }\n" + "        }\n" + "    }\n"
                        + "}";
        appStreamController.onMessage(session, message, "123");
        assertThat(result).hasSize(1)
                .contains("{\"requestId\":null,\"code\":90000001,\"msg\":\"资源不存在: nonexist。\",\"data\":null,"
                        + "\"completed\":true}");
    }

    @Test
    @DisplayName("测试接口直接返回 FitException 异常")
    void testInterfaceThrowFitException() {
        when(command.execute(any(), any())).thenThrow(new AippException(AippErrCode.UNKNOWN));
        appStreamController.onMessage(session, chatMsg, "123");
        assertThat(result).hasSize(1)
                .contains("{\"requestId\":null,\"code\":90000002,\"msg\":\"服务器内部错误，请联系管理员。\",\"data\":null,"
                        + "\"completed\":true}");
    }

    @Test
    @DisplayName("测试接口直接返回其他类型的异常")
    void testInterfaceThrowException() {
        when(command.execute(any(), any())).thenThrow(new RuntimeException("other exception"));
        appStreamController.onMessage(session, chatMsg, "123");
        assertThat(result).hasSize(1)
                .contains("{\"requestId\":null,\"code\":90000002,\"msg\":\"other exception\",\"data\":null,"
                        + "\"completed\":true}");
    }

    @Test
    @DisplayName("测试响应流中出现错误")
    void testFailWhenStream() {
        when(command.execute(any(), any())).thenReturn(Choir.create(emitter -> {
            emitter.emit("emit success");
            emitter.fail(new IOException("emit fail"));
        }));
        appStreamController.onMessage(session, chatMsg, "123");
        assertThat(result).hasSize(2)
                .contains("{\"requestId\":null,\"code\":0,\"msg\":null,\"data\":\"emit success\",\"completed\":false}",
                        "{\"requestId\":null,\"code\":90000002,\"msg\":\"emit fail\",\"data\":null,"
                                + "\"completed\":true}");
    }
}
