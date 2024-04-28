/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.formatters.json;

import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.fel.core.formatters.OutputParser;

import java.lang.reflect.Type;

/**
 * 表示 json 解析器的接口。
 *
 * @param <O> 表示输出对象类型。
 * @author 易文渊
 * @since 2024-04-28
 */
public interface JsonOutputParser<O> extends OutputParser<O> {
    /**
     * 创建默认 json 解析器实例。
     *
     * @param serializer 表示对象序列化器的 {@link ObjectSerializer}。
     * @param type 表示输出类型 {@link E} 的 {@link Type}。
     * @param <E> 表示输出对象类型。
     * @return 表示默认解析器的 {@link JsonOutputParser}。
     */
    static <E> JsonOutputParser<E> create(ObjectSerializer serializer, Type type) {
        return new DefaultJsonOutputParser<>(serializer, type, null);
    }

    /**
     * 创建默认 json 片段解析器实例。
     *
     * @param serializer 表示对象序列化器的 {@link ObjectSerializer}。
     * @param type 表示输出类型 {@link E} 的 {@link Type}。
     * @param <E> 表示输出对象类型。
     * @return 表示默认片段解析器的 {@link JsonOutputParser}。
     */
    static <E> JsonOutputParser<E> createPartial(ObjectSerializer serializer, Type type) {
        return new PartialJsonOutputParser<>(new DefaultJsonOutputParser<>(serializer, type, null));
    }
}