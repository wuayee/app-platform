/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.common.serializer;

import static modelengine.fitframework.inspection.Validation.notNull;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import modelengine.fit.jober.common.serializer.custom.LocalDateTimeDeserializer;
import modelengine.fit.jober.common.serializer.custom.LocalDateTimeSerializer;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.serialization.SerializationException;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

/**
 * jackson对象序列化器
 *
 * @author 梁子涵
 * @since 2024-02-26
 */
@Component()
public class JacksonObjectSerializer implements ObjectSerializer {
    private final ObjectMapper mapper;

    /**
     * 无参构造函数
     */
    public JacksonObjectSerializer() {
        SimpleModule customSerialization = new SimpleModule();
        customSerialization.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        customSerialization.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        this.mapper = JsonMapper.builder(new JsonFactoryBuilder().streamReadConstraints(
                        StreamReadConstraints.builder().maxStringLength(Integer.MAX_VALUE).build()).build())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .addModule(customSerialization)
                .build();
    }

    @Override
    public <T> void serialize(T object, Charset charset, OutputStream out, Map<String, Object> context)
            throws SerializationException {
        notNull(out, "The output stream cannot be null.");
        try (JsonGenerator generator = this.mapper.createGenerator(out, this.getJsonEncoding(charset))) {
            this.mapper.writeValue(generator, object);
        } catch (IOException e) {
            throw new SerializationException("Failed to serialize by Jackson.", e);
        }
    }

    private JsonEncoding getJsonEncoding(Charset charset) {
        for (JsonEncoding jsonEncoding : JsonEncoding.values()) {
            if (StringUtils.equalsIgnoreCase(charset.name(), jsonEncoding.getJavaName())) {
                return jsonEncoding;
            }
        }
        return JsonEncoding.UTF8;
    }

    @Override
    public <T> T deserialize(InputStream in, Charset charset, Type objectType, Map<String, Object> context)
            throws SerializationException {
        notNull(in, "The input stream cannot be null.");
        notNull(objectType, "The object type cannot be null.");
        Map<String, Object> actualContext = ObjectUtils.getIfNull(context, Collections::emptyMap);
        try {
            String read;
            if (actualContext.containsKey("length")) {
                read = IoUtils.content(in, ObjectUtils.<Integer>cast(actualContext.get("length")));
            } else {
                read = IoUtils.content(in);
            }
            return this.mapper.readValue(read, this.mapper.constructType(objectType));
        } catch (IOException e) {
            throw new SerializationException("Failed to deserialize by Jackson.", e);
        }
    }
}
