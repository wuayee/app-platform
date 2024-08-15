/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fit.http.client.support;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.getIfNull;

import com.huawei.fit.http.HttpResource;
import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.http.entity.Entity;
import com.huawei.fit.http.entity.EntityReadException;
import com.huawei.fit.http.entity.ObjectEntity;
import com.huawei.fit.http.entity.TextEntity;
import com.huawei.fit.http.entity.TextEventStreamEntity;
import com.huawei.fit.http.header.ContentType;
import com.huawei.fit.http.protocol.ClientResponse;
import com.huawei.fit.http.protocol.util.BodyUtils;
import com.huawei.fit.http.server.UnsupportedMediaTypeException;
import com.huawei.fit.http.support.AbstractHttpClassicResponse;
import com.huawei.fitframework.exception.ClientException;
import com.huawei.fitframework.util.LazyLoader;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * 表示 {@link HttpClassicClientResponse} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-11-25
 */
public class DefaultHttpClassicClientResponse<T> extends AbstractHttpClassicResponse
        implements HttpClassicClientResponse<T> {
    private final HttpClassicClientFactory.Config config;
    private final ClientResponse clientResponse;
    private final LazyLoader<byte[]> entityBytesLoader = new LazyLoader<>(this::actualEntityBytes);
    private final LazyLoader<Optional<Entity>> entityLoader = new LazyLoader<>(this::actualEntity);
    private final Type responseType;

    /**
     * 创建客户端的 Http 响应的默认实现对象。
     *
     * @param httpResource 表示 Http 的资源的 {@link HttpResource}。
     * @param clientResponse 表示客户端的 Http 响应的 {@link ClientResponse}。
     * @param responseType 表示响应类型的 {@link Type}。
     * @param config 表示 Http 客户端工厂的配置的 {@link HttpClassicClientFactory.Config}。
     */
    public DefaultHttpClassicClientResponse(HttpResource httpResource, ClientResponse clientResponse, Type responseType,
            HttpClassicClientFactory.Config config) {
        super(httpResource,
                notNull(clientResponse, "The client response cannot be null.").startLine(),
                clientResponse.headers());
        this.config = getIfNull(config, () -> HttpClassicClientFactory.Config.builder().build());
        this.clientResponse = clientResponse;
        this.responseType = responseType;
        this.commit();
    }

    @Override
    public Optional<Entity> entity() {
        return this.entityLoader.get();
    }

    @Override
    public byte[] entityBytes() {
        return this.entityBytesLoader.get();
    }

    @Override
    public Optional<ObjectEntity<T>> objectEntity() {
        return ObjectUtils.cast(this.entity());
    }

    @Override
    public Optional<TextEntity> textEntity() {
        return ObjectUtils.cast(this.entity());
    }

    @Override
    public Optional<TextEventStreamEntity> textEventStreamEntity() {
        return ObjectUtils.cast(this.entity());
    }

    private Optional<Entity> actualEntity() {
        Charset charset = this.contentType().flatMap(ContentType::charset).orElse(StandardCharsets.UTF_8);
        try {
            if (this.entityBytesLoader.isLoaded()) {
                byte[] bytes = this.entityBytes();
                return Optional.of(this.entitySerializer(this.responseType).deserializeEntity(bytes, charset, this));
            } else {
                InputStream inputStream = this.clientResponse.getBodyInputStream();
                return Optional.of(this.entitySerializer(this.responseType)
                        .deserializeEntity(inputStream, charset, this));
            }
        } catch (EntityReadException e) {
            throw new UnsupportedMediaTypeException(StringUtils.format("Unsupported media type. [mimeType='{0}']",
                    this.mimeTypeOrDefault().value()), e);
        } finally {
            this.close();
        }
    }

    private byte[] actualEntityBytes() {
        try {
            return BodyUtils.readBody(this.clientResponse.body(), this.headers());
        } catch (IOException e) {
            throw new ClientException("Failed to read body.", e);
        } finally {
            this.close();
        }
    }

    private void close() {
        try {
            this.clientResponse.close();
        } catch (IOException e) {
            // Ignore
        }
    }
}
