/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.http.entity.serializer;

import com.huawei.fit.http.HttpMessage;
import com.huawei.fit.http.entity.EntityReadException;
import com.huawei.fit.http.entity.EntitySerializer;
import com.huawei.fit.http.entity.EntityWriteException;
import com.huawei.fit.http.entity.MultiValueEntity;
import com.huawei.fit.http.entity.support.DefaultMultiValueEntity;
import com.huawei.fit.http.protocol.util.BodyUtils;
import com.huawei.fit.http.util.HttpUtils;
import com.huawei.fitframework.inspection.Nonnull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * 表示消息体格式为 {@code 'application/x-www-form-urlencoded'} 的序列化器。
 *
 * @author 季聿阶
 * @since 2022-10-12
 */
public class FormUrlEncodedEntitySerializer implements EntitySerializer<MultiValueEntity> {
    /** 表示 {@link FormUrlEncodedEntitySerializer} 的单例实现。 */
    public static final EntitySerializer<MultiValueEntity> INSTANCE = new FormUrlEncodedEntitySerializer();

    private FormUrlEncodedEntitySerializer() {}

    @Override
    public void serializeEntity(@Nonnull MultiValueEntity entity, Charset charset, OutputStream out) {
        try {
            out.write(entity.toString().getBytes(charset));
        } catch (IOException e) {
            throw new EntityWriteException("Failed to serialize entity. [mimeType='application/x-www-form-urlencoded']",
                    e);
        }
    }

    @Override
    public MultiValueEntity deserializeEntity(@Nonnull InputStream in, Charset charset,
            @Nonnull HttpMessage httpMessage, Type objectType) {
        try {
            byte[] bytes = BodyUtils.readBody(in, httpMessage.headers());
            String content = new String(bytes, charset);
            return new DefaultMultiValueEntity(httpMessage, HttpUtils.parseQueryOrForm(content));
        } catch (IOException e) {
            throw new EntityReadException(
                    "Failed to deserialize entity. [mimeType='application/x-www-form-urlencoded']", e);
        }
    }
}
