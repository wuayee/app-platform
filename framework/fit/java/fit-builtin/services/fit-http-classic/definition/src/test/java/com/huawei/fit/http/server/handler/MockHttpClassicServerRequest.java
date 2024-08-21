/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.http.server.handler;

import static com.huawei.fit.http.protocol.MimeType.TEXT_PLAIN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.http.HttpResource;
import com.huawei.fit.http.Serializers;
import com.huawei.fit.http.entity.Entity;
import com.huawei.fit.http.entity.EntitySerializer;
import com.huawei.fit.http.entity.support.DefaultNamedEntity;
import com.huawei.fit.http.protocol.MessageHeaderNames;
import com.huawei.fit.http.protocol.ReadableMessageBody;
import com.huawei.fit.http.protocol.RequestLine;
import com.huawei.fit.http.protocol.ServerRequest;
import com.huawei.fit.http.protocol.support.DefaultMessageHeaders;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.support.DefaultHttpClassicServerRequest;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;

import java.io.InputStream;
import java.util.Map;

/**
 * 提供 {@link HttpClassicServerRequest} 的仿真实现。
 *
 * @author 白鹏坤
 * @since 2023-02-17
 */
public class MockHttpClassicServerRequest {
    /**
     * 表示 header 的键。
     */
    public static final String HEADER_KEY = "kk";

    /**
     * 表示 header 的值。
     */
    public static final String HEADER_VALUE = "hello";

    /**
     * 表示 uri 的值。
     */
    public static final String URI_KEY = "k1";

    /**
     * 表示 uri 的值。
     */
    public static final String URI_VALUE = "v1";

    private final DefaultHttpClassicServerRequest request;
    private final Entity entity;

    /**
     * 对经典的服务端的 Http 请求打桩。
     */
    public MockHttpClassicServerRequest() {
        RequestLine startLine = mock(RequestLine.class);
        DefaultMessageHeaders headers = new DefaultMessageHeaders();
        headers.add(HEADER_KEY, HEADER_VALUE);
        headers.add(MessageHeaderNames.CONTENT_TYPE, TEXT_PLAIN.value());
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.startLine()).thenReturn(startLine);
        when(serverRequest.headers()).thenReturn(headers);
        ReadableMessageBody body = mock(ReadableMessageBody.class);
        when(serverRequest.body()).thenReturn(body);
        when(startLine.requestUri()).thenReturn("?" + URI_KEY + "=" + URI_VALUE);
        HttpResource httpResource = mock(HttpResource.class);
        final Serializers serializers = mock(Serializers.class);
        final EntitySerializer<?> entitySerializer = mock(EntitySerializer.class);
        final Map map = MapBuilder.get().put(TEXT_PLAIN, entitySerializer).build();
        this.entity = mock(DefaultNamedEntity.class);
        when(httpResource.serializers()).thenReturn(serializers);
        when(serializers.entities()).thenReturn(map);
        when(entitySerializer.deserializeEntity((byte[]) any(), any(), any())).thenAnswer(ans -> this.entity);
        when(entitySerializer.deserializeEntity(ObjectUtils.<InputStream>cast(any()),
                any(),
                any())).thenAnswer(ans -> this.entity);
        this.request = new DefaultHttpClassicServerRequest(httpResource, serverRequest);
    }

    public DefaultHttpClassicServerRequest getRequest() {
        return this.request;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
