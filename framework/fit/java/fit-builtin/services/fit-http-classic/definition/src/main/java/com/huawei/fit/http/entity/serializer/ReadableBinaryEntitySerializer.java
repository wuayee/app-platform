/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.entity.serializer;

import com.huawei.fit.http.HttpMessage;
import com.huawei.fit.http.entity.EntitySerializer;
import com.huawei.fit.http.entity.EntityWriteException;
import com.huawei.fit.http.entity.ReadableBinaryEntity;
import com.huawei.fit.http.entity.support.DefaultReadableBinaryEntity;
import com.huawei.fitframework.inspection.Nonnull;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * 表示可以任意消息体格式的序列化器。
 *
 * @author 季聿阶
 * @since 2023-10-09
 */
public class ReadableBinaryEntitySerializer implements EntitySerializer<ReadableBinaryEntity> {
    /** 表示 {@link ReadableBinaryEntitySerializer} 的单例实现。 */
    public static final EntitySerializer<ReadableBinaryEntity> INSTANCE = new ReadableBinaryEntitySerializer();

    private ReadableBinaryEntitySerializer() {}

    @Override
    public void serializeEntity(@Nonnull ReadableBinaryEntity entity, Charset charset, OutputStream out) {
        throw new EntityWriteException("Unsupported to serialize entity of Content-Type '*/*'.");
    }

    @Override
    public ReadableBinaryEntity deserializeEntity(@Nonnull InputStream in, Charset charset,
            @Nonnull HttpMessage httpMessage, Type objectType) {
        return new DefaultReadableBinaryEntity(httpMessage, in);
    }
}
