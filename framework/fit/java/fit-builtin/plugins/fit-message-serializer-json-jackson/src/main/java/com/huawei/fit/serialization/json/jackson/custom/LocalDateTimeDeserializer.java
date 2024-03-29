/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.serialization.json.jackson.custom;

import static com.huawei.fitframework.util.StringUtils.blankIf;

import com.huawei.fit.serialization.json.jackson.JacksonObjectSerializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 表示 {@link LocalDateTime} 类型的自定义反序列化器。
 *
 * @author 季聿阶 j00559309
 * @since 2024-02-21
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    private final String dateFormat;

    public LocalDateTimeDeserializer(String dateFormat) {
        this.dateFormat = blankIf(dateFormat, JacksonObjectSerializer.DEFAULT_DATE_TIME_FORMAT);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        return LocalDateTime.parse(jsonParser.getText(), DateTimeFormatter.ofPattern(this.dateFormat));
    }
}
