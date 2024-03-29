/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.protocol.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.http.protocol.HttpVersion;
import com.huawei.fit.http.protocol.RequestLine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link DefaultRequestLine} 的单元测试。
 *
 * @author 杭潇 h00675922
 * @since 2023-02-15
 */
@DisplayName("测试 DefaultRequestLine 类")
public class DefaultRequestLineTest {
    private final HttpVersion httpVersion = HttpVersion.HTTP_1_0;
    private final HttpRequestMethod method = HttpRequestMethod.CONNECT;
    private final String requestUri = "testRequestUri";
    private final RequestLine defaultRequestLine = RequestLine.create(httpVersion, method, requestUri);

    @Test
    @DisplayName("获取的方法与给定的方法值相等")
    void theMethodShouldBeEqualsToTheGivenMethod() {
        HttpRequestMethod actualMethod = this.defaultRequestLine.method();
        assertThat(actualMethod).isEqualTo(this.method);
    }

    @Test
    @DisplayName("获取的请求 Uri 值与给定值相等")
    void theRequestUriShouldBeEqualsToTheGivenUri() {
        String actualUri = this.defaultRequestLine.requestUri();
        assertThat(actualUri).isEqualTo(this.requestUri);
    }

    @Test
    @DisplayName("获取的 Http 版本值与给定的值相等")
    void theHttpVersionShouldBeEqualsToTheGivenVersion() {
        HttpVersion actualHttpVersion = this.defaultRequestLine.httpVersion();
        assertThat(actualHttpVersion).isEqualTo(this.httpVersion);
    }
}
