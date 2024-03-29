/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.http.server.handler;

import static com.sun.jmx.mbeanserver.Util.cast;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.http.HttpResource;
import com.huawei.fit.http.entity.Entity;
import com.huawei.fit.http.entity.support.DefaultObjectEntity;
import com.huawei.fit.http.entity.support.DefaultTextEntity;
import com.huawei.fit.http.protocol.ConfigurableMessageHeaders;
import com.huawei.fit.http.protocol.ConfigurableStatusLine;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.http.protocol.HttpVersion;
import com.huawei.fit.http.protocol.MessageHeaders;
import com.huawei.fit.http.protocol.RequestLine;
import com.huawei.fit.http.protocol.ServerRequest;
import com.huawei.fit.http.protocol.ServerResponse;
import com.huawei.fit.http.protocol.support.DefaultMessageHeaders;
import com.huawei.fit.http.protocol.support.DefaultRequestLine;
import com.huawei.fit.http.protocol.support.DefaultStatusLine;
import com.huawei.fit.http.server.HttpClassicServer;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.http.server.HttpHandler;
import com.huawei.fit.http.server.HttpServerFilter;
import com.huawei.fit.http.server.HttpServerResponseException;
import com.huawei.fit.http.server.handler.support.DefaultHttpExceptionHandler;
import com.huawei.fit.http.server.support.DefaultHttpClassicServerRequest;
import com.huawei.fit.http.server.support.DefaultHttpClassicServerResponse;
import com.huawei.fitframework.annotation.Scope;
import com.huawei.fitframework.serialization.ObjectSerializer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 表示 {@link DefaultReflectibleHttpHandler} 的单元测试。
 *
 * @author 杭潇 h00675922
 * @since 2023-02-24
 */
@DisplayName("测试 DefaultReflectibleHttpHandlers 类")
public class DefaultReflectibleHttpHandlerTest {
    private DefaultReflectibleHttpHandler defaultReflectMappingHandler;
    private HttpClassicServerRequest request;
    private HttpClassicServerResponse response;
    private ServerRequest serverRequest;
    private ServerResponse serverResponse;
    private final Object[] args = new Object[] {1, 2, 3};
    private Method method;
    private List<PropertyValueMapper> propertyValueMappers;

    @BeforeEach
    void setup() {
        this.initializeRequest();
        this.initializeResponse();

        HttpHandler.StaticInfo staticInfo = this.initializeStaticInfo();
        HttpHandler.ExecutionInfo executionInfo = this.initializeExecutionInfo();

        this.defaultReflectMappingHandler = new DefaultReflectibleHttpHandler(staticInfo, executionInfo);
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

    private HttpHandler.StaticInfo initializeStaticInfo() {
        List<PropertyValueMetadata> propertyValueMetadataList = new ArrayList<>();
        PropertyValueMetadata propertyValueMetadata = mock(PropertyValueMetadata.class);
        propertyValueMetadataList.add(propertyValueMetadata);

        return HttpHandler.StaticInfo.builder()
                .pathPattern("pathPattern")
                .propertyValueMetadata(propertyValueMetadataList)
                .build();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private HttpHandler.ExecutionInfo initializeExecutionInfo() {
        HttpClassicServer httpServer = mock(HttpClassicServer.class);
        List<HttpServerFilter> preFilters = new ArrayList<>();
        HttpServerFilter httpServerFilter = mock(HttpServerFilter.class);
        preFilters.add(httpServerFilter);
        this.propertyValueMappers = new ArrayList<>();
        PropertyValueMapper propertyValueMapper = mock(PropertyValueMapper.class);
        this.propertyValueMappers.add(propertyValueMapper);

        Optional<Method> initializeExecutionInfo =
                ReflectionUtils.findMethod(DefaultReflectibleHttpHandlerTest.class, "initializeExecutionInfo");
        this.method = initializeExecutionInfo.get();
        return HttpHandler.ExecutionInfo.builder()
                .httpServer(httpServer)
                .preFilters(preFilters)
                .httpMappers(this.propertyValueMappers)
                .target("testTarget")
                .method(this.method)
                .build();
    }

    @Nested
    @DisplayName("添加客户端 Json 序列化器")
    class AddCustomJsonSerializer {
        @Test
        @DisplayName("给定序列化器值不为 null，执行成功")
        void givenNotNullSerializerThenExecuteSuccessfully() {
            ObjectSerializer objectSerializer = mock(ObjectSerializer.class);
            assertDoesNotThrow(() -> this.addCustomJsonSerializer(objectSerializer));
        }

        private void addCustomJsonSerializer(ObjectSerializer serializer) {
            DefaultReflectibleHttpHandlerTest.this.defaultReflectMappingHandler.addCustomJsonSerializer(serializer);
        }
    }

    @Test
    @DisplayName("执行处理器，执行成功")
    void executeHandleThenExecuteSuccessfully() {
        assertDoesNotThrow(() -> this.defaultReflectMappingHandler.handle(this.request, this.response));
    }

    @Nested
    @DisplayName("执行处理器异常")
    class HandleException {
        @BeforeEach
        void setup() {
            Map<Class<Throwable>, HttpExceptionHandler> exceptionHandlers = new HashMap<>();
            Class<Throwable> throwableClass = Throwable.class;
            DefaultHttpExceptionHandler defaultHttpExceptionHandler = new DefaultHttpExceptionHandler("target",
                    DefaultReflectibleHttpHandlerTest.this.method,
                    200,
                    DefaultReflectibleHttpHandlerTest.this.propertyValueMappers,
                    Scope.PLUGIN);

            exceptionHandlers.put(throwableClass, defaultHttpExceptionHandler);
            DefaultReflectibleHttpHandlerTest.this.defaultReflectMappingHandler.addPluginExceptionHandler(
                    exceptionHandlers);
        }

        @Test
        @DisplayName("给定不可处理的异常值，产生异常，日志记录")
        void givenUnHandledExceptionThenLogIsRecorded() {
            Throwable throwable = new Exception();
            DefaultReflectibleHttpHandlerTest.this.defaultReflectMappingHandler.handleException(
                    DefaultReflectibleHttpHandlerTest.this.request,
                    DefaultReflectibleHttpHandlerTest.this.response,
                    DefaultReflectibleHttpHandlerTest.this.args,
                    throwable);
            Optional<Entity> entity = DefaultReflectibleHttpHandlerTest.this.response.entity();
            assertThat(entity).isPresent().get().isExactlyInstanceOf(DefaultObjectEntity.class);
        }
    }

    @Test
    @DisplayName("给定 Throwable 值为 异常处理器中包含的值，直接返回对应的 value 值")
    void givenThrowableIsContainsInTheExceptionHandlerThenReturnTheValue() {
        Map<Class<Throwable>, HttpExceptionHandler> exceptionHandlers = new HashMap<>();
        Class<Throwable> exceptionClass = cast(Exception.class);
        exceptionHandlers.put(exceptionClass, mock(HttpExceptionHandler.class));
        this.defaultReflectMappingHandler.addPluginExceptionHandler(exceptionHandlers);
        this.defaultReflectMappingHandler.handleException(this.request, this.response, this.args, new Exception());
        Optional<Entity> entity = this.response.entity();
        assertThat(entity).isNotPresent();
    }

    @Test
    @DisplayName("给定抛出值为 HttpServerResponseException 的实例，执行成功")
    void givenThrowableIsInstanceOfHttpServerResponseExceptionThenExecuteSuccessfully() {
        HttpServerResponseException httpServerResponseException =
                new HttpServerResponseException(HttpResponseStatus.MULTI_STATUS, "testInfo");
        this.defaultReflectMappingHandler.handleException(this.request,
                this.response,
                this.args,
                httpServerResponseException);
        Optional<Entity> entity = this.response.entity();
        assertThat(entity).isPresent().get().isExactlyInstanceOf(DefaultObjectEntity.class);
    }

    @Nested
    @DisplayName("测试设置实体方法")
    class TestSetEntity {
        @Test
        @DisplayName("给定结果参数值为 null，返回实体值为 null")
        void givenResultParameterIsNullThenReturnEntityIsNull() {
            DefaultReflectibleHttpHandlerTest.this.defaultReflectMappingHandler.handleResult(
                    DefaultReflectibleHttpHandlerTest.this.request,
                    DefaultReflectibleHttpHandlerTest.this.response,
                    DefaultReflectibleHttpHandlerTest.this.args,
                    null);
            Optional<Entity> entity = DefaultReflectibleHttpHandlerTest.this.response.entity();
            assertThat(entity).isNotPresent();
        }

        @Test
        @DisplayName("给定结果参数值为 String 类型，返回实体值是默认文本实体的实例")
        void givenResultParameterIsStringThenReturnEntityIsInstanceOfDefaultTextEntity() {
            DefaultReflectibleHttpHandlerTest.this.defaultReflectMappingHandler.handleResult(
                    DefaultReflectibleHttpHandlerTest.this.request,
                    DefaultReflectibleHttpHandlerTest.this.response,
                    DefaultReflectibleHttpHandlerTest.this.args,
                    "null");
            Optional<Entity> entity = DefaultReflectibleHttpHandlerTest.this.response.entity();
            assertThat(entity).isPresent().get().isExactlyInstanceOf(DefaultTextEntity.class);
        }
    }
}
