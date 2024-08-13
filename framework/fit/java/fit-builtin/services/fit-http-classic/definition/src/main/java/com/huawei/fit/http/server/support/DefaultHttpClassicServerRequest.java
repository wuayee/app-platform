/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fit.http.server.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.AttributeCollection;
import com.huawei.fit.http.HttpResource;
import com.huawei.fit.http.entity.Entity;
import com.huawei.fit.http.entity.EntityReadException;
import com.huawei.fit.http.header.ContentType;
import com.huawei.fit.http.protocol.Address;
import com.huawei.fit.http.protocol.ServerRequest;
import com.huawei.fit.http.protocol.util.BodyUtils;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.InternalServerErrorException;
import com.huawei.fit.http.server.UnsupportedMediaTypeException;
import com.huawei.fit.http.support.AbstractHttpClassicRequest;
import com.huawei.fit.http.support.DefaultAttributeCollection;
import com.huawei.fitframework.util.LazyLoader;
import com.huawei.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * {@link HttpClassicServerRequest} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-07-08
 */
public class DefaultHttpClassicServerRequest extends AbstractHttpClassicRequest implements HttpClassicServerRequest {
    private final ServerRequest serverRequest;
    private final LazyLoader<byte[]> entityBytesLoader = new LazyLoader<>(this::actualEntityBytes);
    private final LazyLoader<Optional<Entity>> entityLoader = new LazyLoader<>(this::actualEntity);
    private final AttributeCollection attributes = new DefaultAttributeCollection();

    /**
     * 创建经典的服务端的 Http 请求对象。
     *
     * @param httpResource 表示 Http 的资源的 {@link HttpResource}。
     * @param serverRequest 表示服务端的 Http 请求的 {@link ServerRequest}。
     */
    public DefaultHttpClassicServerRequest(HttpResource httpResource, ServerRequest serverRequest) {
        super(httpResource,
                notNull(serverRequest, "The server request cannot be null.").startLine(),
                serverRequest.headers());
        this.serverRequest = serverRequest;
        this.commit();
    }

    @Override
    public Optional<Entity> entity() {
        return this.entityLoader.get();
    }

    @Override
    public AttributeCollection attributes() {
        return this.attributes;
    }

    @Override
    public Address localAddress() {
        return this.serverRequest.localAddress();
    }

    @Override
    public Address remoteAddress() {
        return this.serverRequest.remoteAddress();
    }

    @Override
    public boolean isSecure() {
        return this.serverRequest.isSecure();
    }

    @Override
    public byte[] entityBytes() {
        return this.entityBytesLoader.get();
    }

    private Optional<Entity> actualEntity() {
        Charset charset = this.contentType().flatMap(ContentType::charset).orElse(StandardCharsets.UTF_8);
        try {
            if (this.entityBytesLoader.isLoaded()) {
                byte[] bytes = this.entityBytes();
                return Optional.of(this.entitySerializer().deserializeEntity(bytes, charset, this));
            } else {
                InputStream inputStream = this.serverRequest.getBodyInputStream();
                return Optional.of(this.entitySerializer().deserializeEntity(inputStream, charset, this));
            }
        } catch (EntityReadException e) {
            throw new UnsupportedMediaTypeException(StringUtils.format("Unsupported media type. [mimeType='{0}']",
                    this.mimeTypeOrDefault().value()), e);
        }
    }

    private byte[] actualEntityBytes() {
        try {
            return BodyUtils.readBody(this.serverRequest.body(), this.headers());
        } catch (IOException e) {
            throw new InternalServerErrorException("Failed to read body.", e);
        }
    }

    @Override
    public void close() throws IOException {
        this.serverRequest.close();
        if (this.entityLoader.isLoaded()) {
            Optional<Entity> entity = this.entityLoader.get();
            if (entity.isPresent()) {
                entity.get().close();
            }
        }
    }
}
