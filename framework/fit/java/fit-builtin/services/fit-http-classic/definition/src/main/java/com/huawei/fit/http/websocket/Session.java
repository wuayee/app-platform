/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.websocket;

import com.huawei.fit.http.server.HttpClassicServerRequest;

/**
 * 表示 WebSocket 的会话。
 * <p>该会话信息应该保存在握手请求的属性中，属性名为 {@link com.huawei.fit.http.websocket.server.WebSocketSessionMapper#KEY}。</p>
 *
 * @author 季聿阶 j00559309
 * @since 2023-12-07
 */
public interface Session {
    /**
     * 获取会话的唯一标识。
     *
     * @return 标识会话的唯一标识的 {@link String}。
     */
    String getId();

    /**
     * 获取握手消息的请求。
     *
     * @return 表示握手消息的请求的 {@link HttpClassicServerRequest}。
     */
    HttpClassicServerRequest getHandshakeRequest();

    /**
     * 向会话中以同步的方式发送文本内容。
     *
     * @param text 表示发送的文本内容的 {@link String}。
     */
    void send(String text);

    /**
     * 向会话中以同步的方式发送二进制内容。
     *
     * @param bytes 表示发送的二进制内容的 {@code byte[]}。
     */
    void send(byte[] bytes);

    /**
     * 正常关闭当前会话。
     */
    default void close() {
        this.close(CloseReason.NORMAL_CLOSURE);
    }

    /**
     * 关闭当前会话。
     *
     * @param code 表示关闭的状态码的 {@code int}。
     * @param reason 表示关闭的原因的 {@link String}。
     */
    void close(int code, String reason);

    /**
     * 关闭当前会话。
     *
     * @param closeReason 表示关闭的原因的 {@link CloseReason}。
     */
    void close(CloseReason closeReason);

    /**
     * 获取关闭会话的状态码。
     *
     * @return 表示关闭会话的状态码的 {@code int}。
     */
    int getCloseCode();

    /**
     * 获取关闭会话的原因。
     *
     * @return 表示关闭会话的原因的 {@link String}。
     */
    String getCloseReason();
}
