/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.http.server.handler.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.http.HttpResource;
import com.huawei.fit.http.protocol.ConfigurableMessageHeaders;
import com.huawei.fit.http.protocol.ConfigurableStatusLine;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.http.protocol.HttpVersion;
import com.huawei.fit.http.protocol.MessageHeaders;
import com.huawei.fit.http.protocol.RequestLine;
import com.huawei.fit.http.protocol.ServerRequest;
import com.huawei.fit.http.protocol.ServerResponse;
import com.huawei.fit.http.protocol.support.DefaultMessageHeaders;
import com.huawei.fit.http.protocol.support.DefaultRequestLine;
import com.huawei.fit.http.protocol.support.DefaultStatusLine;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.http.server.handler.PropertyValueMapper;
import com.huawei.fit.http.server.support.DefaultHttpClassicServerRequest;
import com.huawei.fit.http.server.support.DefaultHttpClassicServerResponse;
import com.huawei.fitframework.annotation.Scope;
import com.huawei.fitframework.exception.MethodInvocationException;
import com.huawei.fitframework.util.ReflectionUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 表示 {@link DefaultHttpExceptionHandler} 的单元测试。
 *
 * @author 杭潇 h00675922
 * @since 2023-02-28
 */
@DisplayName("测试 DefaultHttpExceptionHandler 类")
public class DefaultHttpExceptionHandlerTest {
    private DefaultHttpExceptionHandler defaultHttpExceptionHandler;
    private HttpClassicServerRequest request;
    private HttpClassicServerResponse response;
    private ServerRequest serverRequest;
    private ServerResponse serverResponse;

    @BeforeEach
    void setup() {
        Object target = "testTarget";
        int statusCode = 200;
        List<PropertyValueMapper> propertyValueMappers = new ArrayList<>();
        propertyValueMappers.add(mock(PropertyValueMapper.class));
        propertyValueMappers.add(mock(PropertyValueMapper.class));
        propertyValueMappers.add(mock(PropertyValueMapper.class));
        Method method = ReflectionUtils.getDeclaredMethod(DefaultHttpExceptionHandler.class,
                "handle",
                HttpClassicServerRequest.class,
                HttpClassicServerResponse.class,
                Throwable.class);
        this.defaultHttpExceptionHandler =
                new DefaultHttpExceptionHandler(target, method, statusCode, propertyValueMappers, Scope.PLUGIN);
    }

    @AfterEach
    void teardown() throws IOException {
        this.serverRequest.close();
        this.serverResponse.close();
    }

    private void initializeRequest() {
        HttpResource httpResource = mock(HttpResource.class);
        this.serverRequest = mock(ServerRequest.class);
        RequestLine startLine = new DefaultRequestLine(HttpVersion.HTTP_1_0, HttpRequestMethod.CONNECT, "testUri");
        MessageHeaders headers = new DefaultMessageHeaders();
        when(this.serverRequest.startLine()).thenReturn(startLine);
        when(this.serverRequest.headers()).thenReturn(headers);
        this.request = new DefaultHttpClassicServerRequest(httpResource, this.serverRequest);
    }

    private void initializeResponse() {
        this.serverResponse = mock(ServerResponse.class);
        HttpResource httpResource = mock(HttpResource.class);
        ConfigurableStatusLine configurableStatusLine =
                new DefaultStatusLine(HttpVersion.HTTP_1_0, 200, "testReasonPhrase");
        when(this.serverResponse.startLine()).thenReturn(configurableStatusLine);
        ConfigurableMessageHeaders configurableMessageHeaders = new DefaultMessageHeaders();
        when(this.serverResponse.headers()).thenReturn(configurableMessageHeaders);
        this.response = new DefaultHttpClassicServerResponse(httpResource, this.serverResponse);
    }

    @Test
    @DisplayName("给定调用参数不一致，抛出异常")
    void givenInvokeParametersAreInconsistentThenThrowException() {
        this.initializeRequest();
        this.initializeResponse();
        Throwable cause = new Exception();
        MethodInvocationException methodInvocationException =
                catchThrowableOfType(() -> this.defaultHttpExceptionHandler.handle(this.request, this.response, cause),
                        MethodInvocationException.class);
        assertThat(methodInvocationException).isNotNull();
    }
}
