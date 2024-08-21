/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package modelengine.fit.http.server.netty;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.protocol.ConfigurableMessageHeaders;
import modelengine.fit.http.protocol.ConfigurableStatusLine;
import modelengine.fit.http.protocol.HttpVersion;
import modelengine.fit.http.protocol.ServerResponse;
import modelengine.fit.http.protocol.WritableMessageBody;
import modelengine.fit.http.protocol.support.ServerResponseBody;
import modelengine.fitframework.util.StringUtils;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.IOException;
import java.io.OutputStream;

/**
 * {@link ServerResponse} 的 Netty 实现。
 *
 * @author 季聿阶
 * @since 2022-07-08
 */
public class NettyHttpServerResponse implements ServerResponse {
    private final ConfigurableStatusLine startLine;
    private final ConfigurableMessageHeaders headers;
    private final ChannelHandlerContext ctx;
    private final ServerResponseBody body;
    private boolean hasAddedClosedListener;
    private boolean isClosed;

    public NettyHttpServerResponse(ChannelHandlerContext ctx) {
        this.startLine = ConfigurableStatusLine.create(HttpVersion.HTTP_1_1, 0, StringUtils.EMPTY);
        this.headers = ConfigurableMessageHeaders.create();
        this.ctx = notNull(ctx, "The channel handler context cannot be null.");
        this.body = new ServerResponseBody(this);
    }

    @Override
    public ConfigurableStatusLine startLine() {
        return this.startLine;
    }

    @Override
    public ConfigurableMessageHeaders headers() {
        return this.headers;
    }

    @Override
    public WritableMessageBody body() {
        return this.body;
    }

    private void checkIfClosed() throws IOException {
        if (this.isClosed) {
            throw new IOException("The netty http server response has already been closed.");
        }
    }

    @Override
    public void writeStartLineAndHeaders() throws IOException {
        this.checkIfClosed();
        io.netty.handler.codec.http.HttpVersion httpVersion =
                io.netty.handler.codec.http.HttpVersion.valueOf(this.startLine().httpVersion().toString());
        HttpResponseStatus status = HttpResponseStatus.valueOf(this.startLine().statusCode());
        HttpResponse response = new DefaultHttpResponse(httpVersion, status);
        for (String headerName : this.headers().names()) {
            response.headers().set(headerName, this.headers().all(headerName));
        }
        this.ctx.write(response);
    }

    @Override
    public void writeBody(int b) throws IOException {
        this.writeBody(new byte[] {(byte) b});
    }

    @Override
    public void writeBody(byte[] bytes, int off, int len) throws IOException {
        this.checkIfClosed();
        HttpContent httpContent = new DefaultHttpContent(Unpooled.wrappedBuffer(bytes, off, len));
        ChannelFuture channelFuture = this.ctx.writeAndFlush(httpContent);
        try {
            channelFuture.sync();
        } catch (InterruptedException e) {
            throw new IllegalStateException("Failed to write body.", e);
        }
    }

    @Override
    public void flush() throws IOException {
        this.checkIfClosed();
        ChannelFuture channelFuture = this.ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
        if (!this.hasAddedClosedListener) {
            channelFuture.addListener(ChannelFutureListener.CLOSE);
            this.hasAddedClosedListener = true;
        }
    }

    @Override
    public OutputStream getBodyOutputStream() {
        return this.body;
    }

    @Override
    public void close() throws IOException {
        this.isClosed = true;
        this.body.close();
    }
}
