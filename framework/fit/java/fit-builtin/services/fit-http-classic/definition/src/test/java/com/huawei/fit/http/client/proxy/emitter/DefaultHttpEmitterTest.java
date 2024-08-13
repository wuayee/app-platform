/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.http.client.proxy.emitter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.huawei.fit.http.client.HttpClassicClient;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.http.client.proxy.PropertyValueApplier;
import com.huawei.fit.http.client.proxy.RequestBuilder;
import com.huawei.fit.http.protocol.HttpRequestMethod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * 为 {@link  DefaultHttpEmitter} 提供单元测试。
 *
 * @author 王攀博
 * @since 2024-06-13
 */
@DisplayName("测试  DefaultHttpEmitter 接口")
class DefaultHttpEmitterTest {
    private RequestBuilder requestBuilder;
    private HttpClassicClientRequest expectRequest;
    private String protocol;
    private String domain;
    private HttpRequestMethod method;
    private String pathPattern;
    private HttpClassicClient client;

    @BeforeEach
    void setup() {
        this.protocol = "http";
        this.domain = "test_domain";
        this.pathPattern = "/fit/{gid}";
        this.method = HttpRequestMethod.POST;
        this.client = mock(HttpClassicClient.class);
    }

    @Test
    @DisplayName("当提供builder和值时，直接设置值")
    void shouldSetExpectedValueWhenApplyGivenBuilderAndValue() {
        // given
        PropertyValueApplier applier = mock(PropertyValueApplier.class);
        // when
        DefaultHttpEmitter emitter = new DefaultHttpEmitter(Collections.singletonList(applier),
                this.client,
                this.method,
                this.protocol,
                this.domain,
                this.pathPattern);

        Object[] args = {"test_args"};
        HttpClassicClientResponse<?> response = emitter.emit(args);

        // then
        verify(this.client, times(1)).exchange(any(), eq(Object.class));
        verify(applier, times(1)).apply(any(), eq(args[0]));
    }
}
