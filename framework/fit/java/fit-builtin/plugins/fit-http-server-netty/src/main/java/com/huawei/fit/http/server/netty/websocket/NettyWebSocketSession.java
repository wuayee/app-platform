/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.server.netty.websocket;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.websocket.server.WebSocketSessionMapper;
import com.huawei.fit.http.websocket.support.AbstractSession;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * 表示 {@link com.huawei.fit.http.websocket.Session} 的 Netty 的实现。
 *
 * @author 季聿阶 j00559309
 * @since 2023-12-07
 */
public class NettyWebSocketSession extends AbstractSession {
    private final ChannelHandlerContext context;
    private final HttpClassicServerRequest request;

    public NettyWebSocketSession(ChannelHandlerContext context, HttpClassicServerRequest request) {
        this.context = notNull(context, "The netty channel context cannot be null.");
        this.request = notNull(request, "The http classic request cannot be null.");
        this.request.attributes().set(WebSocketSessionMapper.KEY, this);
    }

    @Override
    public HttpClassicServerRequest getHandshakeMessage() {
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
    protected void close0(int code, String reason) {
        this.context.channel().writeAndFlush(new CloseWebSocketFrame(code, reason));
    }
}
