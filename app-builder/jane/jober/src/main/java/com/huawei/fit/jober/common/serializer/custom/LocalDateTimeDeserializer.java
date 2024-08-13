/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.serializer.custom;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 本地时间反序列化器
 *
 * @author 李鑫
 * @since 2024-07-27
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    /**
     * 本地时间反序列化
     *
     * @param jsonParser 表示json解析器
     * @param deserializationContext 表示反序列化上下文
     * @return 本地时间
     * @throws IOException 当发生IO异常时抛出
     * @throws JacksonException 当解决json异常时抛出
     */
    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JacksonException {
        return LocalDateTime.parse(jsonParser.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
