/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.serialization.json.jackson.custom;

import static com.huawei.fitframework.util.StringUtils.blankIf;

import com.huawei.fit.serialization.json.jackson.JacksonObjectSerializer;
import com.huawei.fitframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 表示 {@link ZonedDateTime} 类型的自定义序列化器。
 *
 * @author 季聿阶 j00559309
 * @since 2024-02-21
 */
public class ZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {
    private final String dateFormat;
    private final ZoneId zoneId;

    public ZonedDateTimeSerializer(String dateFormat, String zoneId) {
        this.dateFormat = blankIf(dateFormat, JacksonObjectSerializer.DEFAULT_DATE_TIME_FORMAT);
        this.zoneId = StringUtils.isBlank(zoneId) ? ZoneId.systemDefault() : ZoneId.of(zoneId);
    }

    @Override
    public void serialize(ZonedDateTime zonedDateTime, JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider) throws IOException {
        ZonedDateTime targetZone = zonedDateTime.withZoneSameInstant(this.zoneId);
        jsonGenerator.writeString(targetZone.toLocalDateTime().format(DateTimeFormatter.ofPattern(this.dateFormat)));
    }
}
