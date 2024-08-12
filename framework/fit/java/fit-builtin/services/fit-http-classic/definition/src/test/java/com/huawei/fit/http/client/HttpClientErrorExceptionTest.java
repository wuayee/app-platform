/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.huawei.fit.http.client.support.AbstractHttpClassicClient;
import com.huawei.fit.http.client.support.DefaultHttpClassicClientResponse;
import com.huawei.fit.http.protocol.ClientResponse;
import com.huawei.fit.http.protocol.support.DefaultClientResponse;
import com.huawei.fitframework.model.MultiValueMap;
import com.huawei.fitframework.model.support.DefaultMultiValueMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 为 {@link HttpClientErrorException} 提供单元测试。
 *
 * @author 杭潇
 * @since 2023-02-16
 */
@DisplayName("测试 HttpClientErrorException 类")
public class HttpClientErrorExceptionTest {
    private HttpClassicClientResponse<?> httpClassicClientResponse;
    private int statusCode;
    private String reasonPhrase;

    @BeforeEach
    void setup() throws IOException {
        AbstractHttpClassicClient mock = mock(AbstractHttpClassicClient.class);
        this.statusCode = 200;
        this.reasonPhrase = "testHttpClientErrorException";
        MultiValueMap<String, String> headers = new DefaultMultiValueMap<>();
        headers.add("testKey", "testValue");
        try (InputStream responseStream = new ByteArrayInputStream("TestOfHttpClientErrorException".getBytes(
                StandardCharsets.UTF_8))) {
            ClientResponse clientResponse =
                    new DefaultClientResponse(this.statusCode, this.reasonPhrase, headers, responseStream);
            Class<?> responseType = String.class;
            this.httpClassicClientResponse = new DefaultHttpClassicClientResponse<>(mock,
                    clientResponse,
                    responseType,
                    HttpClassicClientFactory.Config.builder().build());
        }
    }

    @Test
    @DisplayName("给定一个有效的 http 经典客户端响应，初始化对象成功")
    void givenValidHttpClassicClientResponseThenInitializedSuccessfully() {
        HttpClientErrorException httpClientErrorException =
                new HttpClientErrorException(this.httpClassicClientResponse);
        assertThat(httpClientErrorException.statusCode()).isEqualTo(this.statusCode);
        assertThat(httpClientErrorException.getMessage()).isEqualTo(this.reasonPhrase);
    }

    @Test
    @DisplayName("给定有效的 http 经典客户端响应与 Throwable，初始化对象成功")
    void givenValidHttpClassicClientResponseAndThrowableThenInitializedSuccessfully() {
        Throwable throwable = new Throwable("throwSomeThing");
        HttpClientErrorException httpClientErrorException =
                new HttpClientErrorException(this.httpClassicClientResponse, throwable);
        assertThat(httpClientErrorException.statusCode()).isEqualTo(this.statusCode);
        assertThat(httpClientErrorException.getMessage()).isEqualTo(this.reasonPhrase);
    }
}
