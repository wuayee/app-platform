/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.server.netty.websocket;

import static com.huawei.fitframework.inspection.Validation.between;
import static com.huawei.fitframework.inspection.Validation.isTrue;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.websocket.CloseReason;
import com.huawei.fit.http.websocket.Session;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.util.UuidUtils;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.nio.charset.StandardCharsets;

/**
 * 表示 {@link Session} 的 Netty 的实现。
 *
 * @author 季聿阶 j00559309
 * @since 2023-12-07
 */
public class NettyWebSocketSession implements Session {
    private final String id;
    private final ChannelHandlerContext context;
    private final HttpClassicServerRequest request;

    private volatile int closeCode;
    private volatile String closeReason;

    public NettyWebSocketSession(ChannelHandlerContext context, HttpClassicServerRequest request) {
        this.id = UuidUtils.randomUuidString();
        this.context = notNull(context, "The netty channel context cannot be null.");
        this.request = notNull(request, "The http classic request cannot be null.");
        this.request.attributes().set("FIT-WebSocket-Session", this);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public HttpClassicServerRequest getHandshakeRequest() {
        return this.request;
    }

    @Override
    public void send(String text) {
        this.context.channel().writeAndFlush(new TextWebSocketFrame(text));
    }

    @Override
    public void send(byte[] bytes) {
        this.context.channel().writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(bytes)));
    }

    @Override
    public void close(int code, String reason) {
        between(code, 1000, 4999, "The close code is out of range. [code={0}]", code);
        String actualReason = StringUtils.blankIf(reason, StringUtils.EMPTY);
        int length = actualReason.getBytes(StandardCharsets.UTF_8).length;
        isTrue(length <= 123, "The close reason is too long. [length={0}]", length);
        this.closeCode = code;
        this.closeReason = reason;
        this.context.channel().writeAndFlush(new CloseWebSocketFrame(code, actualReason));
        this.context.close();
    }

    @Override
    public void close(CloseReason closeReason) {
        notNull(closeReason, "The close reason cannot be null.");
        this.close(closeReason.getCode(), closeReason.getReason());
    }

    @Override
    public int getCloseCode() {
        return this.closeCode;
    }

    @Override
    public String getCloseReason() {
        return this.closeReason;
    }
}
