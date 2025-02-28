/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.http.call.command;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import lombok.AllArgsConstructor;
import lombok.Data;
import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.entity.ObjectEntity;
import modelengine.fit.http.entity.TextEntity;
import modelengine.fit.http.protocol.MimeType;
import modelengine.fit.jade.aipp.http.call.command.impl.HttpCallCommandHandleImpl;
import modelengine.fitframework.exception.ClientException;
import modelengine.fitframework.exception.TimeoutException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * 表示 {@link HttpCallCommandHandleImpl} 的测试集。
 *
 * @author 张越
 * @since 2024-12-15
 */
@DisplayName("测试 HttpCallCommandHandleImpl")
public class HttpCallCommandHandleImplTest {
    private HttpClassicClient httpClient;
    private HttpClassicClientFactory factory;
    private HttpCallCommandHandler httpCallCommandHandler;

    @BeforeEach
    void setUp() {
        this.httpClient = mock(HttpClassicClient.class);
        this.factory = mock(HttpClassicClientFactory.class);
        this.httpCallCommandHandler = new HttpCallCommandHandleImpl(factory);
    }

    @AfterEach
    void tearDown() {
        reset(this.httpClient);
        reset(this.factory);
    }

    @Test
    @DisplayName("运行成功")
    @SuppressWarnings("unchecked")
    void shouldOk() {
        // given
        when(this.factory.create(any())).thenReturn(this.httpClient);
        HttpClassicClientRequest request = mock(HttpClassicClientRequest.class);
        when(this.httpClient.createRequest(any(), anyString())).thenReturn(request);
        HttpClassicClientResponse<Object> response = mock(HttpClassicClientResponse.class);
        when(this.httpClient.exchange(any(), any())).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        TextEntity textEntity = mock(TextEntity.class);
        when(response.entity()).thenReturn(Optional.of(textEntity));
        when(textEntity.resolvedMimeType()).thenReturn(MimeType.TEXT_PLAIN);
        when(textEntity.content()).thenReturn("aaaaa");

        HttpCallCommand command = this.getCommand();

        // when
        HttpCallResult httpResult = this.httpCallCommandHandler.handle(command);

        // then
        Assertions.assertEquals(200, httpResult.getStatus());
        Assertions.assertNull(httpResult.getErrorMsg());
        Assertions.assertEquals("aaaaa", httpResult.getData());
    }

    @Test
    @DisplayName("返回值为json对象")
    @SuppressWarnings("unchecked")
    void shouldOkWhenReturnObjectIsJson() {
        // given
        when(this.factory.create(any())).thenReturn(this.httpClient);
        HttpClassicClientRequest request = mock(HttpClassicClientRequest.class);
        when(this.httpClient.createRequest(any(), anyString())).thenReturn(request);
        HttpClassicClientResponse<Object> response = mock(HttpClassicClientResponse.class);
        when(this.httpClient.exchange(any(), any())).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        ObjectEntity<Object> objectEntity = mock(ObjectEntity.class);
        when(response.entity()).thenReturn(Optional.of(objectEntity));
        when(objectEntity.resolvedMimeType()).thenReturn(MimeType.APPLICATION_JSON);
        when(objectEntity.object()).thenReturn(new Person("zy", 10));

        HttpCallCommand command = this.getCommand();

        // when
        HttpCallResult httpResult = this.httpCallCommandHandler.handle(command);

        // then
        Assertions.assertEquals(200, httpResult.getStatus());
        Assertions.assertNull(httpResult.getErrorMsg());
        Assertions.assertEquals(Person.class, httpResult.getData().getClass());
    }

    @Test
    @DisplayName("httpCode是200到300之间")
    @SuppressWarnings("unchecked")
    void shouldOkWhenHttpCodeIs2xx() {
        // given
        when(this.factory.create(any())).thenReturn(this.httpClient);
        HttpClassicClientRequest request = mock(HttpClassicClientRequest.class);
        when(this.httpClient.createRequest(any(), anyString())).thenReturn(request);
        HttpClassicClientResponse<Object> response = mock(HttpClassicClientResponse.class);
        when(this.httpClient.exchange(any(), any())).thenReturn(response);
        when(response.statusCode()).thenReturn(201);
        ObjectEntity<Object> objectEntity = mock(ObjectEntity.class);
        when(response.entity()).thenReturn(Optional.of(objectEntity));
        when(objectEntity.resolvedMimeType()).thenReturn(MimeType.APPLICATION_JSON);
        when(objectEntity.object()).thenReturn(new Person("zy", 10));

        HttpCallCommand command = this.getCommand();

        // when
        HttpCallResult httpResult = this.httpCallCommandHandler.handle(command);

        // then
        Assertions.assertEquals(201, httpResult.getStatus());
        Assertions.assertNull(httpResult.getErrorMsg());
        Assertions.assertEquals(Person.class, httpResult.getData().getClass());
    }

    @Test
    @DisplayName("httpCode是300")
    @SuppressWarnings("unchecked")
    void shouldOkWhenHttpCodeIs300() {
        // given
        when(this.factory.create(any())).thenReturn(this.httpClient);
        HttpClassicClientRequest request = mock(HttpClassicClientRequest.class);
        when(this.httpClient.createRequest(any(), anyString())).thenReturn(request);
        HttpClassicClientResponse<Object> response = mock(HttpClassicClientResponse.class);
        when(this.httpClient.exchange(any(), any())).thenReturn(response);
        when(response.statusCode()).thenReturn(300);
        TextEntity textEntity = mock(TextEntity.class);
        when(response.entity()).thenReturn(Optional.of(textEntity));
        when(textEntity.resolvedMimeType()).thenReturn(MimeType.TEXT_PLAIN);
        when(textEntity.content()).thenReturn("http call failed.");

        HttpCallCommand command = this.getCommand();

        // when
        HttpCallResult httpResult = this.httpCallCommandHandler.handle(command);

        // then
        Assertions.assertEquals(300, httpResult.getStatus());
        Assertions.assertNull(httpResult.getData());
        Assertions.assertEquals("http call failed.", httpResult.getErrorMsg());
    }

    @Test
    @DisplayName("抛出TimeoutException")
    void testTimeoutException() {
        // given
        when(this.factory.create(any())).thenReturn(this.httpClient);
        HttpClassicClientRequest request = mock(HttpClassicClientRequest.class);
        when(this.httpClient.createRequest(any(), anyString())).thenReturn(request);
        when(this.httpClient.exchange(any(), any())).thenThrow(TimeoutException.class);

        HttpCallCommand command = this.getCommand();

        // when
        HttpCallResult httpResult = this.httpCallCommandHandler.handle(command);

        // then
        Assertions.assertEquals(TimeoutException.CODE, httpResult.getStatus());
        Assertions.assertEquals("Timeout occurred while executing http call.", httpResult.getErrorMsg());
    }

    @Test
    @DisplayName("抛出ClientException")
    void testClientException() {
        // given
        when(this.factory.create(any())).thenReturn(this.httpClient);
        HttpClassicClientRequest request = mock(HttpClassicClientRequest.class);
        when(this.httpClient.createRequest(any(), anyString())).thenReturn(request);
        when(this.httpClient.exchange(any(), any())).thenThrow(ClientException.class);

        HttpCallCommand command = this.getCommand();

        // when
        HttpCallResult httpResult = this.httpCallCommandHandler.handle(command);

        // then
        Assertions.assertEquals(ClientException.CODE, httpResult.getStatus());
        Assertions.assertEquals("Client exception occurred while executing http call.", httpResult.getErrorMsg());
    }

    private HttpCallCommand getCommand() {
        HttpCallCommand command = new HttpCallCommand();
        command.setMethod("GET");
        command.setUrl("http://examples.com");
        command.setTimeout(1000);
        return command;
    }

    /**
     * 测试使用类.
     *
     * @author 张越
     * @since 2024-12-15
     */
    @Data
    @AllArgsConstructor
    private static class Person {
        private String name;

        private Integer age;
    }
}