/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.http.entity.serializer;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.http.HttpMessage;
import com.huawei.fit.http.entity.EntitySerializer;
import com.huawei.fit.http.entity.TextEvent;
import com.huawei.fit.http.entity.TextEventStreamEntity;
import com.huawei.fit.http.entity.support.DefaultTextEventStreamEntity;
import com.huawei.fitframework.flowable.Choir;
import com.huawei.fitframework.flowable.Emitter;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.serialization.ObjectSerializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 表示消息体格式为 {@code 'text/event-stream'} 的序列化器。
 *
 * @author 易文渊
 * @since 2024-08-03
 */
public class TextEventStreamSerializer implements EntitySerializer<TextEventStreamEntity> {
    private final Type type;
    private final ObjectSerializer jsonSerializer;

    /**
     * 根据 json 序列化器创建 {@link TextEventStreamSerializer} 的实例。
     *
     * @param type 表示数据类型的 {@link Type}。
     * @param jsonSerializer 表示 json 序列化器的 {@link ObjectSerializer}。
     */
    public TextEventStreamSerializer(Type type, ObjectSerializer jsonSerializer) {
        this.type = nullIf(type, String.class);
        this.jsonSerializer = notNull(jsonSerializer, "The json serializer cannot be null.");
    }

    @Override
    public void serializeEntity(@Nonnull TextEventStreamEntity entity, Charset charset, OutputStream out) {
        throw new UnsupportedOperationException("The operation serialize text event stream is nonsupport.");
    }

    @Override
    public TextEventStreamEntity deserializeEntity(@Nonnull InputStream in, Charset charset,
            @Nonnull HttpMessage httpMessage, Type type) {
        Choir<TextEvent> stream = Choir.create(emitter -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                emitData(emitter, reader);
                emitter.complete();
            } catch (Exception e) {
                emitter.fail(e);
            }
        });
        return new DefaultTextEventStreamEntity(httpMessage, stream);
    }

    private void emitData(Emitter<TextEvent> emitter, BufferedReader reader) throws Exception {
        String line;
        StringBuilder dataBuffer = new StringBuilder();
        TextEvent.Builder builder = TextEvent.custom();
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                Object data = this.deserializeData(dataBuffer.toString());
                TextEvent textEvent = builder.data(data).build();
                emitter.emit(textEvent);
                dataBuffer.setLength(0);
                builder = TextEvent.custom();
            } else {
                parseLine(line, builder, dataBuffer);
            }
        }
    }

    private Object deserializeData(String data) {
        if (this.type == String.class) {
            return data;
        }
        return this.jsonSerializer.deserialize(data, this.type);
    }

    private static void parseLine(String line, TextEvent.Builder builder, StringBuilder dataBuffer) {
        String[] parts = line.split(TextEvent.COLON, 2);
        if (parts.length == 2) {
            String key = parts[0];
            String value = parts[1].trim();
            switch (key) {
                case TextEvent.EVENT_ID:
                    builder.id(value);
                    break;
                case TextEvent.EVENT_NAME:
                    builder.event(value);
                    break;
                case TextEvent.EVENT_RETRY:
                    builder.retry(Duration.ofMillis(Long.parseLong(value)));
                    break;
                case TextEvent.EVENT_DATA:
                    if (dataBuffer.length() > 0) {
                        dataBuffer.append(TextEvent.LF);
                    }
                    dataBuffer.append(value);
                    break;
                default:
                    break;
            }
        }
    }
}