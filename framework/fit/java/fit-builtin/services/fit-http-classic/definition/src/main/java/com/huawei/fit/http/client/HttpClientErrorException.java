/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.client;

/**
 * 表示 Http 响应的状态码为 4xx 的异常。
 *
 * @author 季聿阶 j00559309
 * @since 2023-01-29
 */
public class HttpClientErrorException extends HttpClientResponseException {
    public HttpClientErrorException(HttpClassicClientResponse<?> response) {
        super(response);
    }

    public HttpClientErrorException(HttpClassicClientResponse<?> response, Throwable cause) {
        super(response, cause);
    }
}
