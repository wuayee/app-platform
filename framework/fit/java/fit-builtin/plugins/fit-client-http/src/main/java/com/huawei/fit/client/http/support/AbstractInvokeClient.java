/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.client.http.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.client.Request;
import com.huawei.fit.client.http.HttpsConstants;
import com.huawei.fit.client.http.InvokeClient;
import com.huawei.fit.client.http.util.HttpClientUtils;
import com.huawei.fit.http.client.HttpClassicClient;
import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.entity.Entity;
import com.huawei.fit.http.entity.support.DefaultReadableBinaryEntity;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.http.protocol.MessageHeaderNames;
import com.huawei.fit.http.protocol.MimeType;
import com.huawei.fit.http.protocol.Protocol;
import com.huawei.fit.security.Decryptor;
import com.huawei.fit.serialization.MessageSerializer;
import com.huawei.fit.serialization.util.MessageSerializerUtils;
import com.huawei.fitframework.conf.runtime.ClientConfig;
import com.huawei.fitframework.conf.runtime.SerializationFormat;
import com.huawei.fitframework.conf.runtime.WorkerConfig;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 表示 {@link InvokeClient} 的抽象实现类。
 *
 * @author 季聿阶
 * @since 2024-02-17
 */
public abstract class AbstractInvokeClient implements InvokeClient {
    private static final String APPLICATION_JSON = "application/json;charset=UTF-8";

    private final BeanContainer container;
    private final HttpClassicClientFactory factory;
    private final WorkerConfig workerConfig;
    private final ClientConfig clientConfig;

    protected AbstractInvokeClient(BeanContainer container, WorkerConfig workerConfig, ClientConfig clientConfig) {
        this.container = notNull(container, "The bean container cannot be null.");
        this.factory = container.all(HttpClassicClientFactory.class)
                .stream()
                .map(BeanFactory::<HttpClassicClientFactory>get)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("The http classic client factory cannot be null."));
        this.workerConfig = notNull(workerConfig, "The worker config cannot be null.");
        this.clientConfig = notNull(clientConfig, "The http config cannot be null.");
    }

    /**
     * 获取当前的 Bean 容器。
     *
     * @return 表示当前 Bean 容器的 {@link BeanContainer}。
     */
    protected BeanContainer getContainer() {
        return this.container;
    }

    /**
     * 获取当前进程的配置信息。
     *
     * @return 表示当前进程的配置信息的 {@link WorkerConfig}。
     */
    protected WorkerConfig getWorkerConfig() {
        return this.workerConfig;
    }

    /**
     * 构建 Http 客户端。
     *
     * @param request 表示 Http 请求的 {@link Request}。
     * @return 表示构建出来的 Http 客户端的 {@link HttpClassicClient}。
     */
    protected HttpClassicClient buildHttpClient(Request request) {
        Optional<ClientConfig.Secure> secureInfo = this.clientConfig.secure();
        Map<String, Object> config = new HashMap<>();
        if (secureInfo.isPresent()) {
            ClientConfig.Secure secure = secureInfo.get();
            boolean isEncrypted = secure.encrypted();
            String trustStorePassword = secure.trustStorePassword().orElse(StringUtils.EMPTY);
            String keyStorePassword = secure.keyStorePassword().orElse(StringUtils.EMPTY);
            if (isEncrypted) {
                Decryptor decryptor =
                        notNull(this.container.beans().lookup(Decryptor.class), "The Decryptor cannot be null.");
                if (StringUtils.isNotBlank(trustStorePassword)) {
                    trustStorePassword = decryptor.decrypt(trustStorePassword);
                }
                if (StringUtils.isNotBlank(keyStorePassword)) {
                    keyStorePassword = decryptor.decrypt(keyStorePassword);
                }
            }
            secure.keyStoreFile()
                    .ifPresent(keyStore -> config.put(HttpsConstants.CLIENT_SECURE_KEY_STORE_FILE, keyStore));
            secure.trustStoreFile()
                    .ifPresent(trustStore -> config.put(HttpsConstants.CLIENT_SECURE_TRUST_STORE_FILE, trustStore));
            config.put(HttpsConstants.CLIENT_SECURE_IGNORE_TRUST, String.valueOf(secure.ignoreTrust()));
            config.put(HttpsConstants.CLIENT_SECURE_IGNORE_HOSTNAME, String.valueOf(secure.ignoreHostName()));
            config.put(HttpsConstants.CLIENT_SECURE_KEY_STORE_PASSWORD, keyStorePassword);
            config.put(HttpsConstants.CLIENT_SECURE_TRUST_STORE_PASSWORD, trustStorePassword);
        }
        int timeout = this.getTimeout(request);
        return this.factory.create(HttpClassicClientFactory.Config.builder()
                .connectTimeout(timeout)
                .connectionRequestTimeout(timeout)
                .socketTimeout(timeout)
                .custom(config)
                .build());
    }

    /**
     * 获取超时时间。
     * <p>单位为毫秒。</p>
     *
     * @param request 表示 Http 请求的 {@link Request}。
     * @return 表示超时时间的 {@code int}。
     */
    protected int getTimeout(Request request) {
        return Math.toIntExact(request.context().timeoutUnit().toMillis(request.context().timeout()));
    }

    /**
     * 构建 Http 客户端的请求。
     *
     * @param client 表示 Http 客户端的 {@link HttpClassicClient}。
     * @param request 表示 Http 请求的 {@link Request}。
     * @return 表示构建出来的 Http 客户端请求的 {@link HttpClassicClientRequest}。
     */
    protected HttpClassicClientRequest buildClientRequest(HttpClassicClient client, Request request) {
        ConnectionBuilder builder = ConnectionBuilderFactory.getConnectionBuilder(Protocol.from(request.protocol()));
        String url = builder.buildUrl(request);
        HttpClassicClientRequest clientRequest = client.createRequest(HttpRequestMethod.POST, url);
        HttpClientUtils.fillBaseHeaders(clientRequest, request, this.workerConfig);
        return clientRequest;
    }

    /**
     * 构建 Http 的消息体。
     *
     * @param clientRequest 表示 Http 客户端请求的 {@link HttpClassicClientRequest}。
     * @param request 表示 Http 请求的 {@link Request}。
     * @return 表示构建出来的 Http 消息体的 {@link Entity}。
     */
    protected Entity buildHttpEntity(HttpClassicClientRequest clientRequest, Request request) {
        int format = request.metadata().dataFormat();
        if (format == SerializationFormat.JSON.code()) {
            clientRequest.headers().add(MessageHeaderNames.CONTENT_TYPE, APPLICATION_JSON);
        } else {
            clientRequest.headers().add(MessageHeaderNames.CONTENT_TYPE, MimeType.APPLICATION_OCTET_STREAM.value());
        }
        MessageSerializer messageSerializer = MessageSerializerUtils.getMessageSerializer(this.container, format)
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "MessageSerializer required but not found. [format={0}]",
                        format)));
        byte[] bytes = messageSerializer.serializeRequest(request.dataTypes(), request.data());
        clientRequest.headers().add(MessageHeaderNames.CONTENT_LENGTH, Integer.toString(bytes.length));
        return new DefaultReadableBinaryEntity(clientRequest, new ByteArrayInputStream(bytes));
    }
}
