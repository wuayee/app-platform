/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.http.client.proxy.setter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.http.client.HttpClassicClient;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.client.proxy.DestinationSetter;
import com.huawei.fit.http.client.proxy.RequestBuilder;
import com.huawei.fit.http.client.proxy.support.DefaultRequestBuilder;
import com.huawei.fit.http.client.proxy.support.setter.FormUrlEncodedEntitySetter;
import com.huawei.fit.http.entity.MultiValueEntity;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 为 {@link FormUrlEncodedEntitySetter} 提供单元测试。
 *
 * @author 王攀博 w00561424
 * @since 2024-06-12
 */
@DisplayName("测试 FormUrlEncodedEntitySetter 接口")
class FormUrlEncodedEntitySetterTest {
    private String key;
    private String value;
    private RequestBuilder requestBuilder;
    private HttpClassicClientRequest expectRequest;
    private String protocol;
    private String domain;
    private HttpRequestMethod method;
    private String pathPattern;
    private HttpClassicClient client;

    @BeforeEach
    void setup() {
        this.key = "test_key";
        this.value = "test_value";
        this.protocol = "http";
        this.domain = "test_domain";
        this.pathPattern = "/fit/{gid}";
        this.method = HttpRequestMethod.POST;
        this.client = mock(HttpClassicClient.class);
        this.expectRequest = mock(HttpClassicClientRequest.class);
        this.requestBuilder = new DefaultRequestBuilder().client(this.client)
                .protocol(this.protocol)
                .domain(this.domain)
                .pathPattern(this.pathPattern)
                .method(this.method);
        when(client.createRequest(any(), any())).thenReturn(this.expectRequest);
    }

    @AfterEach
    void teardown() {
        this.requestBuilder = null;
    }

    @Test
    @DisplayName("当提供key时，返回entity中的value")
    void shouldReturnValueWhenGetAllByKeyFromFormEntityGivenKey() {
        // given
        MultiValueEntity valueEntity = mock(MultiValueEntity.class);
        when(this.expectRequest.entity()).thenReturn(Optional.ofNullable(valueEntity));
        List<String> list = Collections.singletonList(this.value);
        when(valueEntity.all(anyString())).thenReturn(list);

        // when
        DestinationSetter formUrlEncodedEntitySetter = new FormUrlEncodedEntitySetter(this.key);
        formUrlEncodedEntitySetter.set(this.requestBuilder, this.value);
        MultiValueEntity multiValueEntity = ObjectUtils.cast(this.requestBuilder.build().entity().get());

        // then
        assertThat(multiValueEntity.all(anyString())).isEqualTo(list);
        verify(this.expectRequest, times(1)).formEntity(any());
    }
}
