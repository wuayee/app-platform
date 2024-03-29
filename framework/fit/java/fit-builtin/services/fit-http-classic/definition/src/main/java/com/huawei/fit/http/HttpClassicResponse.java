/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fit.http;

/**
 * 表示经典的 Http 响应。
 *
 * @author 季聿阶 j00559309
 * @since 2022-07-07
 */
public interface HttpClassicResponse extends HttpMessage {
    /**
     * 获取 Http 响应的状态码。
     *
     * @return 表示 Http 响应的状态码的 {@code int}。
     */
    int statusCode();

    /**
     * 获取 Http 响应的状态信息。
     *
     * @return 表示 Http 响应的状态信息的 {@link String}。
     */
    String reasonPhrase();
}
