/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fit.http.client.support;

import static com.huawei.fit.http.protocol.MessageHeaderNames.CONTENT_LENGTH;
import static com.huawei.fit.http.protocol.MessageHeaderNames.COOKIE;
import static com.huawei.fit.http.protocol.MessageHeaderNames.TRANSFER_ENCODING;
import static com.huawei.fit.http.protocol.MessageHeaderValues.CHUNKED;
import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;
import static com.huawei.fitframework.util.ObjectUtils.getIfNull;

import com.huawei.fit.http.HttpResource;
import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.http.entity.Entity;
import com.huawei.fit.http.entity.FileEntity;
import com.huawei.fit.http.entity.ReadableBinaryEntity;
import com.huawei.fit.http.entity.support.DefaultMultiValueEntity;
import com.huawei.fit.http.entity.support.DefaultObjectEntity;
import com.huawei.fit.http.header.ContentType;
import com.huawei.fit.http.protocol.ClientRequest;
import com.huawei.fit.http.protocol.ClientResponse;
import com.huawei.fit.http.protocol.ConfigurableMessageHeaders;
import com.huawei.fit.http.support.AbstractHttpClassicRequest;
import com.huawei.fitframework.exception.ClientException;
import com.huawei.fitframework.model.MultiValueMap;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * 表示 {@link HttpClassicClientRequest} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-11-25
 */
public class DefaultHttpClassicClientRequest extends AbstractHttpClassicRequest implements HttpClassicClientRequest {
    private final HttpClassicClientFactory.Config config;
    private final ClientRequest clientRequest;
    private Entity entity;

    /**
     * 创建经典的客户端的 Http 请求的默认实现对象。
     *
     * @param httpResource 表示 Http 的资源的 {@link HttpResource}。
     * @param clientRequest 表示客户端的 Http 请求的 {@link ClientRequest}。
     * @param config 表示 Http 客户端工厂的配置的 {@link HttpClassicClientFactory.Config}。
     */
    public DefaultHttpClassicClientRequest(HttpResource httpResource, ClientRequest clientRequest,
            HttpClassicClientFactory.Config config) {
        super(httpResource,
                notNull(clientRequest, "The client request cannot be null.").startLine(),
                clientRequest.headers());
        this.config = getIfNull(config, () -> HttpClassicClientFactory.Config.builder().build());
        this.clientRequest = clientRequest;
    }

    @Override
    public ConfigurableMessageHeaders headers() {
        return this.clientRequest.headers();
    }

    @Override
    public Optional<Entity> entity() {
        return Optional.ofNullable(this.entity);
    }

    @Override
    public void entity(Entity entity) {
        if (this.isCommitted()) {
            return;
        }
        this.entity = entity;
        if (this.entity == null) {
            return;
        }
        this.setContentTypeByEntity(this.headers(), this.entity);
    }

    @Override
    public void formEntity(MultiValueMap<String, String> form) {
        if (this.isCommitted()) {
            return;
        }
        Entity multiValueEntity = new DefaultMultiValueEntity(this, form);
        this.entity(multiValueEntity);
    }

    @Override
    public void jsonEntity(Object jsonObject) {
        if (this.isCommitted()) {
            return;
        }
        Entity objectEntity = new DefaultObjectEntity<>(this, jsonObject);
        this.entity(objectEntity);
    }

    @Override
    public HttpClassicClientResponse<Object> exchange() {
        return this.exchange(Object.class);
    }

    @Override
    public <T> HttpClassicClientResponse<T> exchange(Type responseType) {
        this.commit();
        try {
            Charset charset = this.contentType().flatMap(ContentType::charset).orElse(StandardCharsets.UTF_8);
            if (this.entity == null) {
                this.clientRequest.writeStartLineAndHeaders();
            } else if (this.entity instanceof ReadableBinaryEntity) {
                if (this.entity instanceof FileEntity) {
                    FileEntity actual = cast(this.entity);
                    this.headers().set(CONTENT_LENGTH, String.valueOf(actual.length()));
                } else {
                    this.headers().set(TRANSFER_ENCODING, CHUNKED);
                }
                this.clientRequest.writeStartLineAndHeaders();
                ReadableBinaryEntity readableBinaryEntity = cast(this.entity);
                byte[] bytes = new byte[512];
                int read;
                while ((read = readableBinaryEntity.read(bytes)) > -1) {
                    this.clientRequest.writeBody(bytes, 0, read);
                }
            } else {
                byte[] entityBytes = this.entitySerializer().serializeEntity(cast(this.entity), charset);
                this.headers().set(CONTENT_LENGTH, String.valueOf(entityBytes.length));
                this.clientRequest.writeStartLineAndHeaders();
                this.clientRequest.writeBody(entityBytes);
            }
            ClientResponse clientResponse = this.clientRequest.readResponse();
            return new DefaultHttpClassicClientResponse<>(this.httpResource(),
                    clientResponse,
                    responseType,
                    this.config);
        } catch (IOException e) {
            throw new ClientException("Failed to exchange response.", e);
        } finally {
            this.close();
        }
    }

    @Override
    protected void commit() {
        if (this.isCommitted()) {
            return;
        }
        this.headers().set(COOKIE, this.cookies().toString());
        super.commit();
    }

    private void close() {
        try {
            this.clientRequest.close();
            if (this.entity != null) {
                this.entity.close();
                this.entity = null;
            }
        } catch (IOException e) {
            // Ignore
        }
    }
}
