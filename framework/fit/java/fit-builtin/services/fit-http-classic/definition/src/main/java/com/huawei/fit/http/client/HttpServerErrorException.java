/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.client;

/**
 * 表示 Http 响应的状态码为 5xx 的异常。
 *
 * @author 季聿阶
 * @since 2023-01-29
 */
public class HttpServerErrorException extends HttpClientResponseException {
    public HttpServerErrorException(HttpClassicClientResponse<?> response) {
        super(response);
    }

    public HttpServerErrorException(HttpClassicClientResponse<?> response, Throwable cause) {
        super(response, cause);
    }
}
