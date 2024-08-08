/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.http.support;

import static com.huawei.fit.http.protocol.MimeType.APPLICATION_JSON;
import static com.huawei.fit.http.protocol.MimeType.APPLICATION_X_WWW_FORM_URLENCODED;
import static com.huawei.fit.http.protocol.MimeType.MULTIPART_FORM_DATA;
import static com.huawei.fit.http.protocol.MimeType.MULTIPART_MIXED;
import static com.huawei.fit.http.protocol.MimeType.TEXT_EVENT_STREAM;
import static com.huawei.fit.http.protocol.MimeType.TEXT_PLAIN;

import com.huawei.fit.http.Serializers;
import com.huawei.fit.http.entity.EntitySerializer;
import com.huawei.fit.http.entity.ObjectEntity;
import com.huawei.fit.http.entity.TextEventStreamEntity;
import com.huawei.fit.http.protocol.MimeType;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.LazyLoader;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.ObjectUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 表示 {@link Serializers} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2022-11-26
 */
public class DefaultSerializers implements Serializers {
    private final LazyLoader<Map<MimeType, EntitySerializer<?>>> entitySerializersLoader =
            new LazyLoader<>(this::loadEntitySerializers);
    private final Map<String, ObjectSerializer> serializers;

    public DefaultSerializers(Map<String, ObjectSerializer> serializers) {
        this.serializers = ObjectUtils.getIfNull(serializers, HashMap::new);
    }

    @Override
    public Optional<ObjectSerializer> json() {
        return Optional.ofNullable(this.serializers.get("json"));
    }

    @Override
    public <T> Optional<EntitySerializer<ObjectEntity<T>>> jsonEntity(Type type) {
        return this.json().map(jsonSerializer -> EntitySerializer.jsonSerializer(jsonSerializer, type));
    }

    @Override
    public Optional<EntitySerializer<TextEventStreamEntity>> textEventStreamEntity(Type type) {
        return this.json().map(jsonSerializer -> EntitySerializer.textEventStreamSerializer(jsonSerializer, type));
    }

    @Override
    public Map<MimeType, EntitySerializer<?>> entities() {
        return this.entitySerializersLoader.get();
    }

    private Map<MimeType, EntitySerializer<?>> loadEntitySerializers() {
        Map<MimeType, EntitySerializer<?>> curSerializers = MapBuilder.<MimeType, EntitySerializer<?>>get()
                .put(APPLICATION_X_WWW_FORM_URLENCODED, EntitySerializer.formUrlEncodedSerializer())
                .put(MULTIPART_FORM_DATA, EntitySerializer.multiPartSerializer())
                .put(MULTIPART_MIXED, EntitySerializer.multiPartSerializer())
                .put(TEXT_PLAIN, EntitySerializer.textSerializer())
                .build();
        this.json().ifPresent(serializer -> {
            curSerializers.put(APPLICATION_JSON, EntitySerializer.jsonSerializer(serializer));
            curSerializers.put(TEXT_EVENT_STREAM, EntitySerializer.textEventStreamSerializer(serializer, String.class));
        });
        return curSerializers;
    }
}
