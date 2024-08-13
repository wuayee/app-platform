/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.http;

import static com.huawei.fit.http.protocol.MimeType.TEXT_PLAIN;

import com.huawei.fit.http.entity.EntitySerializer;
import com.huawei.fit.http.entity.ObjectEntity;
import com.huawei.fit.http.entity.TextEntity;
import com.huawei.fit.http.entity.TextEventStreamEntity;
import com.huawei.fit.http.protocol.MimeType;
import com.huawei.fit.http.support.DefaultSerializers;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.ObjectUtils;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

/**
 * 表示序列化器的集合。
 *
 * @author 季聿阶
 * @since 2022-08-18
 */
public interface Serializers {
    /**
     * 获取 Json 序列化器。
     *
     * @return 表示 Json 序列化器的 {@link ObjectSerializer}。
     */
    Optional<ObjectSerializer> json();

    /**
     * 获取文本形式的消息体序列化器。
     *
     * @return 表示文本形式的消息体序列化器的 {@link EntitySerializer}{@code <}{@link TextEntity}{@code >}。
     */
    default EntitySerializer<TextEntity> textEntity() {
        return ObjectUtils.cast(this.entities().get(TEXT_PLAIN));
    }

    /**
     * 获取指定类型的 Json 形式的消息体序列化器。
     *
     * @param type 表示指定类型的 {@link Type}。
     * @param <T> 表示指定类型的 {@link T}。
     * @return 表示指定类型的 Json 形式的消息体序列化器的 {@link Optional}{@code <}{@link EntitySerializer}{@code
     * <}{@link ObjectEntity}{@code T}{@code >>>}。
     */
    <T> Optional<EntitySerializer<ObjectEntity<T>>> jsonEntity(Type type);

    /**
     * 获取指定类型的文本事件流形式的消息体序列化器。
     *
     * @param type 表示指定类型的 {@link Type}。
     * @return 表示指定类型的文本事件流形式的消息体序列化器的 {@link Optional}{@code <}{@link EntitySerializer}{@code
     * <}{@link TextEventStreamEntity}{@code >>}。
     */
    Optional<EntitySerializer<TextEventStreamEntity>> textEventStreamEntity(Type type);

    /**
     * 获取消息体的序列化器集合。
     *
     * @return 表示消息体的序列化器集合的 {@link Map}{@code <}{@link MimeType}{@code , }{@link EntitySerializer}{@code
     * <?>>}。
     */
    Map<MimeType, EntitySerializer<?>> entities();

    /**
     * 根据对象序列化器集合，创建序列化器的集合。
     *
     * @param serializers 表示对象序列化器的集合的 {@link Map}{@code <}{@link String}{@code , }{@link
     * ObjectSerializer}{@code >}。
     * @return 表示创建出来的序列化器集合的 {@link Serializers}。
     */
    static Serializers create(Map<String, ObjectSerializer> serializers) {
        return new DefaultSerializers(serializers);
    }
}
