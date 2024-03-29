/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.serialization.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fitframework.serialization.ByteSerializer;
import com.huawei.fitframework.serialization.ResponseMetadataV2;
import com.huawei.fitframework.serialization.SerializationException;
import com.huawei.fitframework.serialization.TagLengthValues;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * {@link DefaultResponseMetadataV2} 的单元测试。
 *
 * @author gwx900499
 * @since 2023-02-17
 */
@DisplayName("测试 DefaultResponseMetadataV2 类")
class DefaultResponseMetadataV2Test {
    private ResponseMetadataV2 metadata;
    private final short version = 4;
    private final byte dataFormat = 5;
    private final int code = 200;
    private final String message = "success";
    private final TagLengthValues tagLengthValues = this.initTagLengthValues();

    private TagLengthValues initTagLengthValues() {
        TagLengthValues values = new DefaultTagLengthValues();
        byte[] bytes = {1, 2, 3};
        values.putTag(1, bytes);
        return values;
    }

    @BeforeEach
    @DisplayName("初始化 DefaultResponseMetadataV2 类")
    void init() {
        this.metadata = ResponseMetadataV2.custom()
                .version(this.version)
                .dataFormat(this.dataFormat)
                .code(this.code)
                .message(this.message)
                .tagValues(this.tagLengthValues)
                .build();
    }

    @Test
    @DisplayName("提供 DefaultResponseMetadataV2 类，返回正常信息")
    void givenDefaultResponseMetadataShouldReturnVersion() {
        byte[] bytes = {1, 2, 3};
        this.tagLengthValues.putTag(1, bytes);
        ResponseMetadataV2 comparedMetadata = ResponseMetadataV2.custom()
                .version(this.version)
                .dataFormat(this.dataFormat)
                .code(this.code)
                .message(this.message)
                .tagValues(this.tagLengthValues)
                .build();
        assertThat(this.metadata).isEqualTo(comparedMetadata);
    }

    @Test
    @DisplayName("提供 ResponseMetadataV2 类 copy 方法时，返回正常信息")
    void givenResponseMetadataWhenCopyThenReturnMetadata() {
        byte[] bytes = {1, 2, 3};
        this.tagLengthValues.putTag(1, bytes);
        ResponseMetadataV2 build = ResponseMetadataV2.custom()
                .version(this.version)
                .dataFormat(this.dataFormat)
                .code(this.code)
                .message(this.message)
                .tagValues(this.tagLengthValues)
                .build();
        ResponseMetadataV2 responseMetadataV2 = build.copy().build();
        assertThat(responseMetadataV2).isEqualTo(this.metadata);
    }

    @Test
    @DisplayName("提供 ResponseMetadataV2 类 deserialize 方法时，返回正常信息")
    void givenRequestMetadataShouldReturnMetadata() {
        byte[] bytes = {1, 2, 3};
        this.tagLengthValues.putTag(1, bytes);
        ResponseMetadataV2 build = ResponseMetadataV2.custom()
                .version(this.version)
                .dataFormat(this.dataFormat)
                .code(this.code)
                .message(this.message)
                .tagValues(this.tagLengthValues)
                .build();
        ResponseMetadataV2 responseMetadataV2 = ResponseMetadataV2.deserialize(build.serialize());
        assertThat(responseMetadataV2).isEqualTo(build);
    }

    @Test
    @DisplayName("提供 DefaultResponseMetadataV2 类 equals 方法与不同类型比较时，返回 false")
    void givenOtherTypeResponseMetadataShouldReturnFalse() {
        byte[] bytes = {1, 2, 3};
        this.tagLengthValues.putTag(1, bytes);
        ResponseMetadataV2 defaultMetadata = ResponseMetadataV2.custom()
                .version(this.version)
                .dataFormat(this.dataFormat)
                .code(this.code)
                .message(this.message)
                .tagValues(null)
                .build();
        ResponseMetadataV2 comparedMetadata = new DefaultResponseMetadataV2.Builder(defaultMetadata).build();
        assertThat(this.metadata.equals(comparedMetadata)).isFalse();
    }

    @Test
    @DisplayName("提供 DefaultResponseMetadataV2 类 toString 方法时，返回正常信息")
    void givenDefaultResponseMetadataShouldReturnStringValue() {
        String meta = this.metadata.toString();
        assertThat(meta).contains(this.message);
    }

    @Test
    @DisplayName("提供 ResponseMetadataV2 类序列化方法时，返回正常信息")
    void givenResponseMetadataWhenSerializeThenReturnMetadata() throws IOException {
        byte[] bytes = {1, 2, 3};
        this.tagLengthValues.putTag(1, bytes);
        ResponseMetadataV2 defaultMetadata = ResponseMetadataV2.custom()
                .version(this.version)
                .dataFormat(this.dataFormat)
                .code(this.code)
                .message(this.message)
                .tagValues(null)
                .build();
        DefaultResponseMetadataV2.Serializer serializer = DefaultResponseMetadataV2.Serializer.INSTANCE;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            serializer.serialize(defaultMetadata, out);
            try (InputStream in = new ByteArrayInputStream(out.toByteArray())) {
                ResponseMetadataV2 expected = serializer.deserialize(in);
                assertThat(defaultMetadata).isEqualTo(expected);
            }
        }
    }

    @Test
    @DisplayName("提供 ByteSerializer 类序列化方法异常时，抛出异常")
    void givenByteSerializerWhenSerializeExceptionThrowException() throws IOException {
        byte[] bytes = {1, 2, 3};
        ByteSerializer<?> byteSerializer = mock(ByteSerializer.class);
        when(byteSerializer.deserialize(any(ByteArrayInputStream.class))).thenThrow(new IOException());
        assertThatThrownBy(() -> ByteSerializer.deserialize(byteSerializer,
                bytes)).isInstanceOf(SerializationException.class);
    }
}
