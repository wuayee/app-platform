/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.http.client;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.http.Serializers;
import com.huawei.fit.http.entity.ObjectEntity;
import com.huawei.fit.http.entity.TextEntity;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fitframework.value.ValueFetcher;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * {@link HttpClassicClient} 的实现类
 *
 * @author 杭潇 h00675922
 * @since 2023-02-20
 */
public class HttpClassicClientImplement implements HttpClassicClient {
    /**
     * 创建 Http 客户端对象。
     */
    public HttpClassicClientImplement() {}

    @Override
    public Serializers serializers() {
        return null;
    }

    @Override
    public ValueFetcher valueFetcher() {
        return null;
    }

    @Override
    public HttpClassicClientRequest createRequest(HttpRequestMethod method, String url) {
        return null;
    }

    @Override
    public HttpClassicClientResponse<Object> exchange(HttpClassicClientRequest request) {
        return this.exchange(request, Object.class);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <T> HttpClassicClientResponse<T> exchange(HttpClassicClientRequest request, Type responseType) {
        notNull(request, "The http classic request to exchange cannot be null.");
        HttpClassicClientResponse httpClassicClientResponse = mock(HttpClassicClientResponse.class);
        ObjectEntity objectEntity = mock(ObjectEntity.class);
        when(objectEntity.object()).thenReturn(24);
        Optional<ObjectEntity> optionalObjectEntity = Optional.of(objectEntity);
        when(httpClassicClientResponse.objectEntity()).thenReturn(optionalObjectEntity);
        TextEntity textEntity = mock(TextEntity.class);
        when(textEntity.content()).thenReturn("finishTextEntity");
        Optional<TextEntity> optionalTextEntity = Optional.of(textEntity);
        when(httpClassicClientResponse.textEntity()).thenReturn(optionalTextEntity);
        int statusCode = request.exchange(responseType).statusCode();
        when(httpClassicClientResponse.statusCode()).thenReturn(statusCode);
        return httpClassicClientResponse;
    }
}
