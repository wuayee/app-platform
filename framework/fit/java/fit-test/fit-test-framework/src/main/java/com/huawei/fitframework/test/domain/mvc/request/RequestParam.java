/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.test.domain.mvc.request;

import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.protocol.HttpRequestMethod;

import java.lang.reflect.Type;

/**
 * 封装客户端的请求参数。
 *
 * @author 王攀博 w00561424
 * @since 2024-04-09
 */
public class RequestParam {
    private final Type responseType;
    private final HttpClassicClientRequest request;

    public RequestParam(HttpRequestMethod method, String url, Type responseType, HttpClassicClientRequest request) {
        this.responseType = responseType;
        this.request = request;
    }

    /**
     * 获取请求结构体中的返回结果类型。
     *
     * @return 表示返回结果的类型 {@link Type}。
     */
    public Type getResponseType() {
        return this.responseType;
    }

    /**
     * 获取请求结构体中的返回结果类型。
     *
     * @return 表示返回结果的类型 {@link Type}。
     */
    public HttpClassicClientRequest getRequest() {
        return this.request;
    }
}
