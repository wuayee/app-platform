/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.http.client;

import static com.huawei.fitframework.inspection.Validation.notNull;

/**
 * 表示携带 Http 响应的异常。
 *
 * @author 季聿阶
 * @since 2023-01-29
 */
public class HttpClientResponseException extends HttpClientException {
    private final HttpClassicClientResponse<?> response;

    /**
     * 创建携带 Http 响应的异常对象。
     *
     * @param response 表示 Http 响应的 {@link HttpClassicClientResponse}{@code <?>}。
     */
    public HttpClientResponseException(HttpClassicClientResponse<?> response) {
        this(response, null);
    }

    /**
     * 创建携带 Http 响应的异常对象。
     *
     * @param response 表示 Http 响应的 {@link HttpClassicClientResponse}{@code <?>}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public HttpClientResponseException(HttpClassicClientResponse<?> response, Throwable cause) {
        super(notNull(response, "The http response cannot be null.").reasonPhrase(), cause);
        this.response = response;
    }

    /**
     * 获取 Http 响应的状态码。
     *
     * @return 表示 Http 响应的状态码的 {@code int}。
     */
    public int statusCode() {
        return this.response.statusCode();
    }
}
