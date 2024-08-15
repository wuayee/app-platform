/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fit.http.server;

import com.huawei.fit.http.AttributeCollection;
import com.huawei.fit.http.HttpClassicRequest;
import com.huawei.fit.http.HttpResource;
import com.huawei.fit.http.protocol.Address;
import com.huawei.fit.http.protocol.ServerRequest;
import com.huawei.fit.http.server.support.DefaultHttpClassicServerRequest;

import java.io.Closeable;

/**
 * 表示经典的服务端的 Http 请求。
 *
 * @author 季聿阶
 * @since 2022-07-07
 */
public interface HttpClassicServerRequest extends HttpClassicRequest, Closeable {
    /**
     * 获取 Http 请求的所有属性集合。
     *
     * @return 表示 Http 请求的所有属性集合的 {@link AttributeCollection}。
     */
    AttributeCollection attributes();

    /**
     * 表示 Http 请求的本地地址。
     *
     * @return 表示本地地址的 {@link Address}。
     */
    Address localAddress();

    /**
     * 表示 Http 请求的远端地址。
     *
     * @return 表示远端地址的 {@link Address}。
     */
    Address remoteAddress();

    /**
     * 获取 Http 请求是否为安全的的标记。
     *
     * @return 如果 Http 请求安全，则返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isSecure();

    /**
     * 获取 Http 消息的消息体的结构化数据的二进制内容。
     *
     * @return 表示消息体的结构化数据的二进制内容的 {@code byte[]}。
     */
    byte[] entityBytes();

    /**
     * 创建经典的服务端的 Http 请求对象。
     *
     * @param httpResource 表示 Http 的资源的 {@link HttpResource}。
     * @param serverRequest 表示服务端的 Http 请求的 {@link ServerRequest}。
     * @return 表示创建出来的经典的服务端的 Http 请求对象的 {@link HttpClassicServerRequest}。
     */
    static HttpClassicServerRequest create(HttpResource httpResource, ServerRequest serverRequest) {
        return new DefaultHttpClassicServerRequest(httpResource, serverRequest);
    }
}
