/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.common.serializer.custom;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 本地时间序列化器
 *
 * @author 李鑫
 * @since 2024-07-27
 */
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
    /**
     * 本地时间反序列化
     *
     * @param localDateTime 表示本地时间
     * @param jsonGenerator 表示json生成器
     * @param serializerProvider 表示反序列化提供者
     * @throws IOException 当发生IO异常时抛出
     */
    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
