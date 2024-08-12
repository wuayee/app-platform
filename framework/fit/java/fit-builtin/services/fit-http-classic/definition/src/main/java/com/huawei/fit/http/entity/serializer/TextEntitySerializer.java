/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.http.entity.serializer;

import com.huawei.fit.http.HttpMessage;
import com.huawei.fit.http.entity.EntityReadException;
import com.huawei.fit.http.entity.EntitySerializer;
import com.huawei.fit.http.entity.EntityWriteException;
import com.huawei.fit.http.entity.TextEntity;
import com.huawei.fit.http.entity.support.DefaultTextEntity;
import com.huawei.fit.http.protocol.util.BodyUtils;
import com.huawei.fitframework.inspection.Nonnull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * 表示消息体格式为 {@code 'text/plain'} 的序列化器。
 *
 * @author 季聿阶
 * @since 2022-10-11
 */
public class TextEntitySerializer implements EntitySerializer<TextEntity> {
    /** 表示 {@link TextEntitySerializer} 的单例实现。 */
    public static final EntitySerializer<TextEntity> INSTANCE = new TextEntitySerializer();

    private TextEntitySerializer() {}

    @Override
    public void serializeEntity(@Nonnull TextEntity entity, Charset charset, OutputStream out) {
        try {
            out.write(entity.content().getBytes(charset));
        } catch (IOException e) {
            throw new EntityWriteException("Failed to serialize entity. [mimeType='text/plain']", e);
        }
    }

    @Override
    public TextEntity deserializeEntity(@Nonnull InputStream in, Charset charset, @Nonnull HttpMessage httpMessage,
            Type objectType) {
        try {
            byte[] bytes = BodyUtils.readBody(in, httpMessage.headers());
            String content = new String(bytes, charset);
            return new DefaultTextEntity(httpMessage, content);
        } catch (IOException e) {
            throw new EntityReadException("Failed to deserialize entity. [mimeType='text/plain']", e);
        }
    }
}
