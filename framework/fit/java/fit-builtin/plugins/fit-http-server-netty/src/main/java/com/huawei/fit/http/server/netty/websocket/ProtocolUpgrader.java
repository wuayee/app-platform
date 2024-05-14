/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.server.netty.websocket;

import static com.huawei.fit.http.HttpClassicRequestAttribute.PATH_PATTERN;
import static com.huawei.fit.http.protocol.MessageHeaderValues.WEBSOCKET;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.protocol.MessageHeaderNames;
import com.huawei.fit.http.protocol.MessageHeaderValues;
import com.huawei.fit.http.server.HttpClassicServer;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.netty.NettyHttpServerRequest;
import com.huawei.fit.http.server.support.DefaultHttpClassicServerRequest;
import com.huawei.fit.http.websocket.server.WebSocketHandler;
import com.huawei.fitframework.util.StringUtils;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * 表示 Http 协议的升级处理器。
 *
 * @author 季聿阶 j00559309
 * @since 2023-12-07
 */
@ChannelHandler.Sharable
public class ProtocolUpgrader extends ChannelInboundHandlerAdapter {
    private static final int MAX_CONTENT_LENGTH = 8192;

    private final HttpClassicServer server;
    private final boolean secure;
    private final long largeBodySize;

    public ProtocolUpgrader(HttpClassicServer server, boolean secure, long largeBodySize) {
        this.server = notNull(server, "The http server cannot be null.");
        this.secure = secure;
        this.largeBodySize = largeBodySize;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            if (isWebSocketUpgrade(req)) {
                String path = req.uri();
                NettyHttpServerRequest serverRequest =
                        new NettyHttpServerRequest(req, ctx, this.secure, this.largeBodySize);
                HttpClassicServerRequest classicRequest =
                        new DefaultHttpClassicServerRequest(this.server, serverRequest);
                WebSocketHandler handler = this.server.webSocketDispatcher().dispatch(classicRequest);
                classicRequest.attributes().set(PATH_PATTERN.key(), handler.pathPattern());
                // 先移除 HttpClassicRequestAssembler，确保 Http 请求的处理器被去除
                ctx.pipeline().removeLast();
                // 再动态添加 WebSocket 的处理器
                ctx.pipeline().addLast(new HttpObjectAggregator(MAX_CONTENT_LENGTH));
                ctx.pipeline().addLast(new WebSocketServerProtocolHandler(path));
                ctx.pipeline().addLast(new NettyWebSocketHandler(ctx, handler, classicRequest));
                // 最后将当前处理器移除，因为一个通道只会发生一次 Http 协议升级
                ctx.pipeline().remove(this);
            }
        }
        super.channelRead(ctx, msg);
    }

    private static boolean isWebSocketUpgrade(HttpRequest req) {
        return req.method() == HttpMethod.GET && StringUtils.equalsIgnoreCase(req.headers()
                .get(MessageHeaderNames.CONNECTION), MessageHeaderValues.UPGRADE)
                && StringUtils.equalsIgnoreCase(req.headers().get(MessageHeaderNames.UPGRADE), WEBSOCKET);
    }
}
