/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.http.entity.TextEntity;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

/**
 * HttpUtils工具的测试类
 *
 * @since 2024-07-31
 * @author f00881613
 */
public class HttpUtilTest {
    @Test
    void testExecute() {
        HttpClassicClientRequest requestMock = mock(HttpClassicClientRequest.class);
        HttpClassicClientResponse<Object> responseMock = mock(HttpClassicClientResponse.class);
        when(requestMock.exchange()).thenReturn(responseMock);
    }

    @Test
    void testSendHttpRequest() {
        HttpClassicClientRequest requestMock = mock(HttpClassicClientRequest.class);
        HttpClassicClientResponse<Object> responseMock = mock(HttpClassicClientResponse.class);
        TextEntity textEntityMock = mock(TextEntity.class);
        when(requestMock.exchange()).thenReturn(responseMock);
        when(responseMock.statusCode()).thenReturn(200);
        when(responseMock.textEntity()).thenReturn(Optional.of(textEntityMock));
        when(textEntityMock.content()).thenReturn("test");
        try {
            assertEquals("test", HttpUtils.sendHttpRequest(requestMock));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testSendHttpRequestWithWrongStatusCode() {
        HttpClassicClientRequest requestMock = mock(HttpClassicClientRequest.class);
        HttpClassicClientResponse<Object> responseMock = mock(HttpClassicClientResponse.class);
        when(requestMock.exchange()).thenReturn(responseMock);
        when(responseMock.statusCode()).thenReturn(404);
        when(requestMock.requestUri()).thenReturn("");
        assertThrows(IOException.class, () -> HttpUtils.sendHttpRequest(requestMock));
    }

    @Test
    void testEmptyEntity() {
        HttpClassicClientRequest requestMock = mock(HttpClassicClientRequest.class);
        HttpClassicClientResponse<Object> responseMock = mock(HttpClassicClientResponse.class);
        when(requestMock.exchange()).thenReturn(responseMock);
        when(responseMock.statusCode()).thenReturn(200);
        when(responseMock.textEntity()).thenReturn(Optional.empty());
        when(requestMock.requestUri()).thenReturn("");
        assertThrows(IOException.class, () -> HttpUtils.sendHttpRequest(requestMock));
    }

    @Test
    void testGetConfig() {
        int socketTimeout = 5000;
        HttpClassicClientFactory.Config config = HttpUtils.requestConfig(socketTimeout);
        assertEquals(5000, config.socketTimeout());
    }
}
