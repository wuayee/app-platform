/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.server.netty.websocket;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.websocket.CloseReason;
import com.huawei.fit.http.websocket.Session;
import com.huawei.fit.http.websocket.server.WebSocketHandler;
import com.huawei.fitframework.log.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * 表示 Netty 用于处理 WebSocket 消息的处理器。
 *
 * @author 季聿阶
 * @since 2023-12-07
 */
public class NettyWebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private static final Logger log = Logger.get(NettyWebSocketHandler.class);

    private final WebSocketHandler handler;
    private final Session session;

    public NettyWebSocketHandler(ChannelHandlerContext ctx, WebSocketHandler handler,
            HttpClassicServerRequest request) {
        this.handler = notNull(handler, "The websocket handler cannot be null.");
        this.session = new NettyWebSocketSession(ctx, request);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        try {
            this.handler.onError(this.session, cause);
        } catch (Throwable e) {
            log.error("Failed to handle websocket by netty worker.", e);
            this.session.close(CloseReason.UNEXPECTED_CONDITION);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) {
        if (msg instanceof TextWebSocketFrame) {
            TextWebSocketFrame frame = (TextWebSocketFrame) msg;
            try {
                this.handler.onMessage(this.session, frame.text());
            } catch (Throwable e) {
                this.handler.onError(this.session, e);
            }
            return;
        }
        if (msg instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame frame = (BinaryWebSocketFrame) msg;
            try {
                ByteBuf byteBuf = frame.content();
                byte[] binMsg = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(binMsg);
                this.handler.onMessage(this.session, binMsg);
            } catch (Throwable e) {
                this.handler.onError(this.session, e);
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            this.handler.onOpen(this.session);
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.handler.onClose(this.session);
        super.channelInactive(ctx);
    }
}
