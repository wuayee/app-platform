/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.http.server;

/**
 * 表示 Http 请求过滤器的调用链。
 *
 * @author 季聿阶
 * @since 2022-07-06
 */
public interface HttpServerFilterChain {
    /**
     * 继续执行下一个过滤器。
     *
     * @param request 表示当前 Http 请求的 {@link HttpClassicServerRequest}。
     * @param response 表示当前 Http 响应的 {@link HttpClassicServerResponse}。
     * @throws HttpServerException 当执行当前请求发生异常时。
     */
    void doFilter(HttpClassicServerRequest request, HttpClassicServerResponse response) throws HttpServerException;
}
