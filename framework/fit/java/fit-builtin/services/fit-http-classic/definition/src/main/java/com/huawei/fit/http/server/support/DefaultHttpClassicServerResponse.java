/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fit.http.server.support;

import static com.huawei.fit.http.protocol.MessageHeaderNames.CACHE_CONTROL;
import static com.huawei.fit.http.protocol.MessageHeaderNames.CONNECTION;
import static com.huawei.fit.http.protocol.MessageHeaderNames.CONTENT_DISPOSITION;
import static com.huawei.fit.http.protocol.MessageHeaderNames.CONTENT_LENGTH;
import static com.huawei.fit.http.protocol.MessageHeaderNames.COOKIE;
import static com.huawei.fit.http.protocol.MessageHeaderNames.TRANSFER_ENCODING;
import static com.huawei.fit.http.protocol.MessageHeaderValues.CHUNKED;
import static com.huawei.fit.http.protocol.MessageHeaderValues.KEEP_ALIVE;
import static com.huawei.fit.http.protocol.MessageHeaderValues.NO_CACHE;
import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.http.HttpResource;
import com.huawei.fit.http.entity.Entity;
import com.huawei.fit.http.entity.FileEntity;
import com.huawei.fit.http.entity.ReadableBinaryEntity;
import com.huawei.fit.http.entity.TextEventStreamEntity;
import com.huawei.fit.http.entity.WritableBinaryEntity;
import com.huawei.fit.http.entity.support.DefaultWritableBinaryEntity;
import com.huawei.fit.http.header.ContentDisposition;
import com.huawei.fit.http.header.ContentType;
import com.huawei.fit.http.header.HeaderValue;
import com.huawei.fit.http.header.ParameterCollection;
import com.huawei.fit.http.protocol.ConfigurableMessageHeaders;
import com.huawei.fit.http.protocol.MessageHeaderValues;
import com.huawei.fit.http.protocol.ServerResponse;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.http.server.InternalServerErrorException;
import com.huawei.fit.http.support.AbstractHttpClassicResponse;
import com.huawei.fitframework.resource.UrlUtils;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 表示 {@link HttpClassicServerResponse} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @author 易文渊 y00612997
 * @since 2022-11-25
 */
public class DefaultHttpClassicServerResponse extends AbstractHttpClassicResponse implements HttpClassicServerResponse {
    private static final String FILENAME_PARAMETER_KEY = "filename";
    private static final String FILENAME_STAR_PARAMETER_KEY = "filename*";

    private final ServerResponse serverResponse;
    private Entity entity;

    /**
     * 创建经典的服务端的 Http 响应对象。
     *
     * @param httpResource 表示 Http 的资源的 {@link HttpResource}。
     * @param serverResponse 表示服务端的 Http 响应的 {@link ServerResponse}。
     */
    public DefaultHttpClassicServerResponse(HttpResource httpResource, ServerResponse serverResponse) {
        super(httpResource,
                notNull(serverResponse, "The server response cannot be null.").startLine(),
                serverResponse.headers());
        this.serverResponse = serverResponse;
    }

    @Override
    public Optional<Entity> entity() {
        return Optional.ofNullable(this.entity);
    }

    @Override
    public void statusCode(int statusCode) {
        if (this.isCommitted()) {
            return;
        }
        this.serverResponse.startLine().statusCode(statusCode);
    }

    @Override
    public void reasonPhrase(String reasonPhrase) {
        if (this.isCommitted()) {
            return;
        }
        this.serverResponse.startLine().reasonPhrase(reasonPhrase);
    }

    @Override
    public ConfigurableMessageHeaders headers() {
        return this.serverResponse.headers();
    }

    @Override
    public void entity(Entity entity) {
        if (this.isCommitted()) {
            return;
        }
        this.entity = entity;
    }

    private void setFileEntityHeaders(ConfigurableMessageHeaders headers, FileEntity fileEntity) {
        HeaderValue headerValue = fileEntity.isAttachment() ? HeaderValue.create(MessageHeaderValues.ATTACHMENT,
                createAttachedFileName(fileEntity)) : HeaderValue.create(MessageHeaderValues.INLINE);
        ContentDisposition contentDisposition = headerValue.toContentDisposition();
        headers.set(CONTENT_DISPOSITION, contentDisposition.toString());
    }

    private static ParameterCollection createAttachedFileName(FileEntity fileEntity) {
        boolean isAscii = StringUtils.isAscii(fileEntity.filename());
        if (isAscii) {
            return ParameterCollection.create().set(FILENAME_PARAMETER_KEY, "\"" + fileEntity.filename() + "\"");
        } else {
            String encodedFilename = UrlUtils.encodeValue(fileEntity.filename());
            return ParameterCollection.create().set(FILENAME_STAR_PARAMETER_KEY, "UTF-8''" + encodedFilename);
        }
    }

    @Override
    public WritableBinaryEntity writableBinaryEntity() throws IOException {
        if (this.isCommitted()) {
            throw new InternalServerErrorException("The http classic server response has already committed.");
        }
        this.entity = new DefaultWritableBinaryEntity(this, this.serverResponse);
        this.commit();
        this.serverResponse.writeStartLineAndHeaders();
        return ObjectUtils.cast(this.entity);
    }

    @Override
    public void send() {
        this.commit();
        try {
            Charset charset = this.contentType().flatMap(ContentType::charset).orElse(StandardCharsets.UTF_8);
            if (this.entity == null) {
                this.serverResponse.writeStartLineAndHeaders();
            } else if (this.entity instanceof ReadableBinaryEntity) {
                if (this.entity instanceof FileEntity) {
                    FileEntity actual = cast(this.entity);
                    this.headers().set(CONTENT_LENGTH, String.valueOf(actual.length()));
                } else if (!this.headers().contains(CONTENT_LENGTH)) {
                    this.headers().set(TRANSFER_ENCODING, CHUNKED);
                }
                this.serverResponse.writeStartLineAndHeaders();
                ReadableBinaryEntity readableBinaryEntity = cast(this.entity);
                byte[] bytes = new byte[512];
                int read;
                while ((read = readableBinaryEntity.read(bytes)) > -1) {
                    this.serverResponse.writeBody(bytes, 0, read);
                }
            } else if (this.entity instanceof WritableBinaryEntity) {
                // WritableBinaryEntity 已经在用户代码层面进行了输出，因此此处什么都不需要处理。
            } else if (this.entity instanceof TextEventStreamEntity) {
                this.headers().set(CACHE_CONTROL, NO_CACHE);
                this.headers().set(CONNECTION, KEEP_ALIVE);
                this.serverResponse.writeStartLineAndHeaders();
                this.sendTextEventStream(cast(this.entity));
            } else {
                byte[] entityBytes = this.entitySerializer().serializeEntity(ObjectUtils.cast(this.entity), charset);
                this.headers().set(CONTENT_LENGTH, String.valueOf(entityBytes.length));
                this.serverResponse.writeStartLineAndHeaders();
                this.serverResponse.writeBody(entityBytes);
            }
            this.serverResponse.flush();
        } catch (IOException e) {
            throw new InternalServerErrorException("Failed to write response.", e);
        }
    }

    private void sendTextEventStream(TextEventStreamEntity eventStreamEntity) throws IOException {
        ObjectSerializer objectSerializer = eventStreamEntity.belongTo()
                .jsonSerializer()
                .orElseThrow(() -> new IllegalStateException("The serializer cannot be null."));
        AtomicReference<Exception> exception = new AtomicReference<>();
        eventStreamEntity.stream()
                .map(sse -> sse.serialize(objectSerializer).getBytes(StandardCharsets.UTF_8))
                .subscribe(null, (subscription, bytes) -> {
                    try {
                        this.serverResponse.writeBody(bytes);
                    } catch (IOException e) {
                        subscription.cancel();
                        exception.set(e);
                    }
                }, null, ((ignore, e) -> exception.set(e)));
        Exception e = exception.get();
        if (e == null) {
            return;
        }
        if (e instanceof IOException) {
            throw (IOException) e;
        }
        throw new InternalServerErrorException("Failed to execute handler.", e);
    }

    @Override
    protected void commit() {
        if (this.isCommitted()) {
            return;
        }
        this.headers().set(COOKIE, this.cookies().toString());
        if (this.entity != null) {
            this.setContentTypeByEntity(this.headers(), this.entity);
            if (this.entity instanceof FileEntity) {
                this.setFileEntityHeaders(this.headers(), ObjectUtils.cast(this.entity));
            }
        }
        super.commit();
    }

    @Override
    public void close() throws IOException {
        this.serverResponse.close();
        if (this.entity != null) {
            this.entity.close();
            this.entity = null;
        }
    }
}
