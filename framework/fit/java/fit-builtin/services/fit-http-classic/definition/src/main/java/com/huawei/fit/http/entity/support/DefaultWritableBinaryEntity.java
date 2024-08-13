/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.http.entity.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.HttpMessage;
import com.huawei.fit.http.entity.WritableBinaryEntity;
import com.huawei.fit.http.protocol.MimeType;
import com.huawei.fit.http.protocol.ServerResponse;
import com.huawei.fitframework.inspection.Nonnull;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 表示 {@link WritableBinaryEntity} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-01-28
 */
public class DefaultWritableBinaryEntity extends AbstractEntity implements WritableBinaryEntity {
    private final ServerResponse serverResponse;

    /**
     * 创建只写的二进制的消息体数据对象。
     *
     * @param httpMessage 表示消息体数据所属的 Http 消息的 {@link HttpMessage}。
     * @param serverResponse 表示服务端的 Http 响应的 {@link ServerResponse}，用于消息体数据的写入。
     */
    public DefaultWritableBinaryEntity(HttpMessage httpMessage, ServerResponse serverResponse) {
        super(httpMessage);
        this.serverResponse = notNull(serverResponse, "The server response cannot be null.");
    }

    @Override
    public void write(byte[] bytes, int off, int len) throws IOException {
        this.serverResponse.writeBody(bytes, off, len);
    }

    @Override
    public void flush() throws IOException {
        this.serverResponse.flush();
    }

    @Override
    public OutputStream getOutputStream() {
        return this.serverResponse.getBodyOutputStream();
    }

    @Nonnull
    @Override
    public MimeType resolvedMimeType() {
        return MimeType.APPLICATION_OCTET_STREAM;
    }
}
