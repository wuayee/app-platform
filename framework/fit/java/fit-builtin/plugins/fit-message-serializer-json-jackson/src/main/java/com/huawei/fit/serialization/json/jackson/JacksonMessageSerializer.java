/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 */

package com.huawei.fit.serialization.json.jackson;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.huawei.fit.serialization.MessageSerializer;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.conf.runtime.SerializationFormat;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.serialization.SerializationException;
import com.huawei.fitframework.util.ArrayUtils;
import com.huawei.fitframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 表示 {@link MessageSerializer} 的 Jackson 的实现。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-11-23
 */
@Component
public class JacksonMessageSerializer implements MessageSerializer {
    private final ObjectSerializer serializer;
    private final ObjectMapper mapper;

    /**
     * 构造一个新的 {@link JacksonMessageSerializer} 实例。
     *
     * @param serializer 表示用于序列化和反序列化实例的 {@link ObjectSerializer}。
     */
    public JacksonMessageSerializer(@Fit(alias = "jackson") ObjectSerializer serializer) {
        this.serializer = notNull(serializer, "The Jackson serializer cannot be null.");
        JacksonObjectSerializer jacksonObjectSerializer = cast(this.serializer);
        this.mapper = jacksonObjectSerializer.getMapper();
    }

    @Override
    public byte[] serializeRequest(Type[] argumentTypes, Object[] arguments) {
        return this.serializer.serialize(arguments, UTF_8);
    }

    @Override
    public Object[] deserializeRequest(Type[] argumentTypes, byte[] serialized) {
        ArrayNode array;
        try {
            array = this.mapper.readValue(serialized, ArrayNode.class);
        } catch (IOException e) {
            throw new SerializationException("Failed to read JSON request from serialized bytes.", e);
        }
        if (array.size() != argumentTypes.length) {
            throw new SerializationException(StringUtils.format("Total {0} arguments supplied but {1} required.",
                    array.size(),
                    argumentTypes.length));
        }
        Object[] arguments = new Object[argumentTypes.length];
        for (int i = 0; i < argumentTypes.length; i++) {
            arguments[i] = this.mapper.convertValue(array.get(i), this.mapper.constructType(argumentTypes[i]));
        }
        return arguments;
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
        return SerializationFormat.JSON.code();
    }
}
