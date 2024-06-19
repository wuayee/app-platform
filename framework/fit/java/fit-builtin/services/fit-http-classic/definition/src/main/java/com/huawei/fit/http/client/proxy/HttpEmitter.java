/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.http.client.proxy;

import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.http.client.HttpClientException;

/**
 * 表示客户端执行的代理。
 *
 * @author 王攀博 w00561424
 * @since 2024-06-11
 */
public interface HttpEmitter {
    /**
     * 表示根据参数发出请求并获取返回结果。
     *
     * @param args 表示客户端请求的参数的 {@link Object}{@code []}。
     * @param <T> 表示期待的返回值类型的 {@link T}。
     * @return 表示 Http 响应的 {@link HttpClassicClientResponse}{@code <}{@link T}{@code >}。
     * @throws HttpClientException 当客户端请求发生异常时。
     */
    <T> HttpClassicClientResponse<T> emit(Object[] args) throws HttpClientException;
}
