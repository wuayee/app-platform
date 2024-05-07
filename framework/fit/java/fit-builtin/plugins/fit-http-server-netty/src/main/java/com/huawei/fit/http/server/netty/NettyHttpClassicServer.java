/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fit.http.server.netty;

import static com.huawei.fit.http.protocol.util.SslUtils.getKeyManagers;
import static com.huawei.fit.http.protocol.util.SslUtils.getSslContext;
import static com.huawei.fit.http.protocol.util.SslUtils.getTrustManagers;
import static com.huawei.fitframework.inspection.Validation.greaterThan;
import static com.huawei.fitframework.inspection.Validation.isTrue;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.Serializers;
import com.huawei.fit.http.server.HttpClassicServer;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.http.server.HttpDispatcher;
import com.huawei.fit.http.server.HttpServerStartupException;
import com.huawei.fit.security.Decryptor;
import com.huawei.fit.server.http.HttpConfig;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.conf.runtime.ServerConfig;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.thread.DefaultThreadFactory;
import com.huawei.fitframework.util.LockUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.util.ThreadUtils;
import com.huawei.fitframework.value.ValueFetcher;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;

/**
 * {@link HttpClassicServer} 的 Netty 实现。
 *
 * @author 季聿阶 j00559309
 * @since 2022-07-08
 */
@Component
public class NettyHttpClassicServer implements HttpClassicServer {
    private static final Logger log = Logger.get(NettyHttpClassicServer.class);

    private final BeanContainer container;
    private final HttpDispatcher dispatcher;
    private final Serializers serializers;
    private final ValueFetcher valueFetcher;

    private final int coreThreadNum;
    private final int maxThreadNum;
    private final int queueCapacity;
    private final long largeBodySize;
    private final NettyHttpServerConfig nettyConfig;
    private final ServerConfig.Secure httpsConfig;

    private final ThreadPoolExecutor startServerExecutor = ThreadUtils.singleThreadPool(new DefaultThreadFactory(
            "netty-http-server",
            false,
            (thread, exception) -> log.error("Failed to start netty http server.", exception)));
    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private volatile int httpPort;
    private volatile int httpsPort;
    private final boolean isGracefulExit;
    private volatile boolean isStarted = false;
    private final Lock lock = LockUtils.newReentrantLock();

    NettyHttpClassicServer(BeanContainer container, Map<String, ObjectSerializer> serializers,
            ValueFetcher valueFetcher, NettyHttpServerConfig nettyConfig, HttpConfig httpConfig,
            @Value("${worker.exit.graceful}") boolean isGracefulExit) {
        this.container = notNull(container, "The bean container cannot be null.");
        this.dispatcher = HttpDispatcher.create();
        notNull(serializers, "The serializers cannot be null.");
        this.serializers = Serializers.create(serializers);
        this.valueFetcher = notNull(valueFetcher, "The value fetcher cannot be null.");
        this.nettyConfig = notNull(nettyConfig, "The netty http server config cannot be null.");
        this.coreThreadNum = this.nettyConfig.getCoreThreadNum() > 0
                ? this.nettyConfig.getCoreThreadNum()
                : Math.max(NettyRuntime.availableProcessors() * 2, 10);
        this.maxThreadNum = Math.max(this.nettyConfig.getMaxThreadNum(), this.coreThreadNum);
        this.queueCapacity = Math.max(this.nettyConfig.getQueueCapacity(), 0);
        this.isGracefulExit = isGracefulExit;
        this.httpsConfig = notNull(httpConfig, "The http config cannot be null.").secure().orElse(null);
        this.largeBodySize = httpConfig.largeBodySize();
    }

    @Override
    public HttpClassicServer bind(int port) {
        return this.bind(port, false);
    }

    @Override
    public HttpClassicServer bind(int port, boolean isSecure) {
        if (this.isStarted) {
            return this;
        }
        if (isSecure) {
            this.httpsPort = greaterThan(port,
                    0,
                    "The port to bind to netty http server cannot be less than 1. [port={0}, isSecure={1}]",
                    port,
                    true);
        } else {
            this.httpPort = greaterThan(port,
                    0,
                    "The port to bind to netty http server cannot be less than 1. [port={0}, isSecure={1}]",
                    port,
                    false);
        }
        return this;
    }

    @Override
    public void start() {
        if (this.isStarted) {
            return;
        }
        isTrue(this.httpPort > 0 || this.httpsPort > 0,
                "At least 1 port should be bound to netty http server. [httpPort={0}, httpsPort={1}]",
                this.httpPort,
                this.httpsPort);
        LockUtils.synchronize(this.lock, () -> {
            if (!this.isStarted) {
                this.startServerExecutor.execute(this::startServer);
            }
        });
    }

    @Override
    public boolean isStarted() {
        return this.isStarted;
    }

    @Override
    public void stop() {
        if (!this.isStarted) {
            return;
        }
        LockUtils.synchronize(this.lock, () -> {
            if (!this.isStarted) {
                return;
            }
            try {
                this.channelGroup.close().sync();
            } catch (InterruptedException e) {
                // ignored
            }
            this.startServerExecutor.shutdownNow();
            log.info("Terminate http server successfully.");
            this.isStarted = false;
        });
    }

    private void startServer() {
        EventLoopGroup bossGroup = createBossGroup();
        EventLoopGroup workerGroup = this.createWorkerGroup();
        try {
            HttpClassicRequestAssembler.Config assemblerConfig = this.getAssemblerConfig();
            HttpClassicRequestAssembler httpClassicRequestAssembler =
                    new HttpClassicRequestAssembler(this, false, assemblerConfig);
            HttpClassicRequestAssembler httpsClassicRequestAssembler =
                    new HttpClassicRequestAssembler(this, true, assemblerConfig);
            SSLContext sslContext = this.httpsPort > 0 ? this.createSslContext() : null;
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            if (ch.localAddress().getPort() == NettyHttpClassicServer.this.httpsPort
                                    && sslContext != null) {
                                SSLEngine sslEngine = sslContext.createSSLEngine();
                                sslEngine.setUseClientMode(false);
                                sslEngine.setNeedClientAuth(httpsConfig.needClientAuth());
                                pipeline.addLast(new SslHandler(sslEngine));
                                pipeline.addLast(new HttpServerCodec());
                                pipeline.addLast(httpsClassicRequestAssembler);
                            } else {
                                pipeline.addLast(new HttpServerCodec());
                                pipeline.addLast(httpClassicRequestAssembler);
                            }
                        }
                    });
            this.logServerStarted();
            if (this.httpPort > 0) {
                Channel channel = serverBootstrap.bind(this.httpPort).sync().channel();
                this.channelGroup.add(channel);
            }
            if (this.httpsPort > 0) {
                Channel channel = serverBootstrap.bind(this.httpsPort).sync().channel();
                this.channelGroup.add(channel);
            }
            ChannelGroupFuture channelFutures = this.channelGroup.newCloseFuture();
            this.isStarted = true;
            channelFutures.sync();
        } catch (InterruptedException | GeneralSecurityException | IOException e) {
            throw new HttpServerStartupException("Netty http server is interrupted.", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            log.info("Http server has been terminated.");
        }
    }

    private HttpClassicRequestAssembler.Config getAssemblerConfig() {
        return HttpClassicRequestAssembler.Config.custom()
                .shouldDisplayError(this.nettyConfig.isDisplayError())
                .largeBodySize(this.largeBodySize)
                .coreThreadNum(this.coreThreadNum)
                .maxThreadNum(this.maxThreadNum)
                .queueCapacity(this.queueCapacity)
                .isGracefulExit(this.isGracefulExit)
                .build();
    }

    private void logServerStarted() {
        if (this.httpPort > 0 && this.httpsPort > 0) {
            log.info("Start netty http server successfully. [httpPort={}, httpsPort={}]",
                    this.httpPort,
                    this.httpsPort);
        } else if (this.httpPort > 0) {
            log.info("Start netty http server successfully. [httpPort={}]", this.httpPort);
        } else {
            log.info("Start netty http server successfully. [httpsPort={}]", this.httpsPort);
        }
    }

    private static EventLoopGroup createBossGroup() {
        return new NioEventLoopGroup(1,
                new DefaultThreadFactory("netty-boss-group",
                        false,
                        (thread, exception) -> log.error("Netty boss group occurs exception.", exception)));
    }

    private EventLoopGroup createWorkerGroup() {
        boolean isDaemon = !this.isGracefulExit;
        return new NioEventLoopGroup(this.coreThreadNum,
                new DefaultThreadFactory("netty-worker-group",
                        isDaemon,
                        (thread, exception) -> log.error("Netty worker group occurs exception.", exception)));
    }

    private SSLContext createSslContext() throws GeneralSecurityException, IOException {
        notNull(this.httpsConfig, "The https config cannot be null.");
        String trustStorePassword = this.httpsConfig.trustStorePassword().orElse(StringUtils.EMPTY);
        String keyStorePassword = this.httpsConfig.keyStorePassword().orElse(StringUtils.EMPTY);
        if (this.httpsConfig.encrypted()) {
            Decryptor decryptor =
                    notNull(this.container.beans().lookup(Decryptor.class), "The decryptor cannot be null.");
            trustStorePassword = decryptor.decrypt(trustStorePassword);
            keyStorePassword = decryptor.decrypt(keyStorePassword);
        }
        String trustStoreFile = this.httpsConfig.trustStoreFile().orElse(StringUtils.EMPTY);
        String keyStoreFile = this.httpsConfig.keyStoreFile().orElse(StringUtils.EMPTY);
        KeyManager[] keyManagers = getKeyManagers(keyStoreFile, keyStorePassword);
        TrustManager[] trustManagers = getTrustManagers(trustStoreFile, trustStorePassword);
        return getSslContext(keyManagers, trustManagers);
    }

    @Override
    public HttpDispatcher dispatcher() {
        return this.dispatcher;
    }

    @Override
    public void send(HttpClassicServerResponse response) {
        notNull(response, "The http classic response to send cannot be null.");
        response.send();
    }

    @Override
    public Serializers serializers() {
        return this.serializers;
    }

    @Override
    public ValueFetcher valueFetcher() {
        return this.valueFetcher;
    }
}
