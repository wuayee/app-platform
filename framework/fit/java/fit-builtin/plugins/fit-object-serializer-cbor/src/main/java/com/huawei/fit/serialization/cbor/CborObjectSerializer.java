/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.serialization.cbor;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.serialization.SerializationException;
import com.huawei.fitframework.serialization.annotation.BuiltinSerializer;
import com.huawei.fitframework.util.ObjectUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * 表示 {@link ObjectSerializer} 的 CBOR 的实现。
 * <p><a href="https://datatracker.ietf.org/doc/html/rfc8949">RFC 8949</a> 列出了 CBOR 的详细规范。</p>
 *
 * @author 季聿阶 j00559309
 * @since 2024-01-25
 */
@Component("cbor")
@BuiltinSerializer
public class CborObjectSerializer implements ObjectSerializer {
    private final CborEncoder encoder = new CborEncoder();
    private final CborDecoder decoder = new CborDecoder();

    @Override
    public <T> void serialize(T object, Charset charset, OutputStream out, Map<String, Object> context)
            throws SerializationException {
        try {
            this.encoder.encode(object, out);
        } catch (IOException e) {
            throw new SerializationException("Failed to serialize by CBOR.", e);
        }
    }

    @Override
    public <T> T deserialize(InputStream in, Charset charset, Type objectType, Map<String, Object> context)
            throws SerializationException {
        try {
            Object decoded = this.decoder.decode(in);
            return ObjectUtils.toCustomObject(decoded, objectType);
        } catch (IOException e) {
            throw new SerializationException("Failed to deserialize by CBOR.", e);
        }
    }
}
