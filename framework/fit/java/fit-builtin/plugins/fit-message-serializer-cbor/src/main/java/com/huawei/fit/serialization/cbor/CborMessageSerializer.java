/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.serialization.cbor;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.huawei.fit.serialization.MessageSerializer;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Order;
import com.huawei.fitframework.conf.runtime.SerializationFormat;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.ArrayUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.TypeUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 表示 {@link MessageSerializer} 的 CBOR 的实现。
 * <p><a href="https://datatracker.ietf.org/doc/html/rfc8949">RFC 8949</a> 列出了 CBOR 的详细规范。</p>
 *
 * @author 季聿阶
 * @since 2024-01-25
 */
@Order(Order.LOW)
@Component
public class CborMessageSerializer implements MessageSerializer {
    private final ObjectSerializer serializer;

    public CborMessageSerializer(@Fit(alias = "cbor") ObjectSerializer serializer) {
        this.serializer = notNull(serializer, "The CBOR serializer cannot be null.");
    }

    @Override
    public byte[] serializeRequest(Type[] argumentTypes, Object[] arguments) {
        return this.serializer.serialize(arguments, UTF_8);
    }

    @Override
    public Object[] deserializeRequest(Type[] argumentTypes, byte[] serialized) {
        List<Object> deserialized = this.serializer.deserialize(serialized,
                UTF_8,
                TypeUtils.parameterized(List.class, new Type[] {Object.class}));
        Object[] array = new Object[argumentTypes.length];
        for (int i = 0; i < deserialized.size(); i++) {
            array[i] = ObjectUtils.toCustomObject(deserialized.get(i), argumentTypes[i]);
        }
        return array;
    }

    @Override
    public <T> byte[] serializeResponse(Type returnType, T returnData) {
        return this.serializer.serialize(returnData, UTF_8);
    }

    @Override
    public <T> T deserializeResponse(Type returnType, byte[] serialized) {
        if (ArrayUtils.isEmpty(serialized)) {
            return null;
        }
        return this.serializer.deserialize(serialized, UTF_8, returnType);
    }

    @Override
    public boolean isSupported(Method method) {
        return true;
    }

    @Override
    public int getFormat() {
        return SerializationFormat.CBOR.code();
    }
}
