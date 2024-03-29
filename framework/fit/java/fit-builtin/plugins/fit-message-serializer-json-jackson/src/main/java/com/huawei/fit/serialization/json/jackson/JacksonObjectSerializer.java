/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.serialization.json.jackson;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.serialization.json.jackson.custom.LocalDateDeserializer;
import com.huawei.fit.serialization.json.jackson.custom.LocalDateSerializer;
import com.huawei.fit.serialization.json.jackson.custom.LocalDateTimeDeserializer;
import com.huawei.fit.serialization.json.jackson.custom.LocalDateTimeSerializer;
import com.huawei.fit.serialization.json.jackson.custom.ZonedDateTimeDeserializer;
import com.huawei.fit.serialization.json.jackson.custom.ZonedDateTimeSerializer;
import com.huawei.fitframework.annotation.Alias;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.serialization.SerializationException;
import com.huawei.fitframework.serialization.annotation.BuiltinSerializer;
import com.huawei.fitframework.util.IoUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;

/**
 * 表示 Json 格式的序列化器。
 *
 * @author 季聿阶 j00559309
 * @since 2022-08-03
 */
@Component("json")
@Alias("jackson")
@BuiltinSerializer
public class JacksonObjectSerializer implements ObjectSerializer {
    /** 表示默认的日期时间的格式。 */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** 表示默认的日期格式。 */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    private final ObjectMapper mapper;

    public JacksonObjectSerializer(@Value("${date-time-format}") String dateTimeFormat,
            @Value("${date-format}") String dateFormat, @Value("${time-zone}") String zoneId) {
        this.mapper = new ObjectMapper(new JsonFactoryBuilder().streamReadConstraints(StreamReadConstraints.builder()
                .maxStringLength(Integer.MAX_VALUE)
                .build()).build()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormat));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormat));
        module.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(dateTimeFormat, zoneId));
        module.addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer(dateTimeFormat, zoneId));
        module.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormat));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormat));
        this.mapper.registerModule(module);
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
        Type actualType = ObjectUtils.nullIf(objectType, Object.class);
        Map<String, Object> actualContext = ObjectUtils.getIfNull(context, Collections::emptyMap);
        try {
            String read;
            if (actualContext.containsKey("length")) {
                read = IoUtils.content(in, ObjectUtils.<Integer>cast(actualContext.get("length")));
            } else {
                read = IoUtils.content(in);
            }
            return this.mapper.readValue(read, this.mapper.constructType(actualType));
        } catch (IOException e) {
            throw new SerializationException("Failed to deserialize by Jackson.", e);
        }
    }

    /**
     * 获取 Jackson 的核心序列化器。
     * <p>因为需要保证 Jackson 的序列化配置全局统一，因此需要将配置好的序列化器透出，让其他 Jackson 序列化器使用。</p>
     *
     * @return 表示 Jackson 的核心序列化器的 {@link ObjectMapper}。
     */
    ObjectMapper getMapper() {
        return this.mapper;
    }
}
