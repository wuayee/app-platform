/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
 */

package com.huawei.fitframework.serialization.support;

import static com.huawei.fitframework.util.ObjectUtils.getIfNull;

import com.huawei.fitframework.serialization.ByteSerializer;
import com.huawei.fitframework.serialization.ResponseMetadataV2;
import com.huawei.fitframework.serialization.TagLengthValues;
import com.huawei.fitframework.util.Convert;
import com.huawei.fitframework.util.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

/**
 * 为 {@link ResponseMetadataV2} 提供默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2021-05-14
 */
public class DefaultResponseMetadataV2 implements ResponseMetadataV2 {
    private final short version;
    private final byte dataFormat;
    private final int code;
    private final String message;
    private final TagLengthValues tagValues;

    private DefaultResponseMetadataV2(short version, byte dataFormat, int code, String message,
            TagLengthValues tagLengthValues) {
        this.version = version;
        this.dataFormat = dataFormat;
        this.code = code;
        this.message = message;
        this.tagValues = getIfNull(tagLengthValues, TagLengthValues::create);
    }

    @Override
    public short version() {
        return this.version;
    }

    @Override
    public byte dataFormatByte() {
        return this.dataFormat;
    }

    @Override
    public int dataFormat() {
        return this.dataFormat;
    }

    @Override
    public int code() {
        return this.code;
    }

    @Override
    public String message() {
        return this.message;
    }

    @Override
    public TagLengthValues tagValues() {
        return this.tagValues;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.serialize());
    }

    @Override
    public boolean equals(Object another) {
        if (another instanceof ResponseMetadataV2) {
            return this.hashCode() == another.hashCode();
        }
        return false;
    }

    @Override
    public String toString() {
        return "{\"version\": " + this.version + ", \"dataFormat\": " + this.dataFormat + ", \"code\": " + this.code
                + ", \"message\": \"" + this.message + "\", \"tagValues\": " + this.tagValues + '}';
    }

    /**
     * 为创建 {@link DefaultResponseMetadataV2} 的实例提供构建器。
     *
     * @author 季聿阶 j00559309
     * @since 2021-05-14
     */
    public static final class Builder implements ResponseMetadataV2.Builder {
        private short version;
        private byte dataFormat;
        private int code;
        private String message;
        private TagLengthValues tagValues;

        /**
         * 使用已知的远端调用元数据初始化 {@link Builder} 类的新实例。
         *
         * @param metadata 表示已知的远端调用的元数据的 {@link ResponseMetadataV2}。构建器中会以该实例中的现有数据作为初始值。
         */
        public Builder(ResponseMetadataV2 metadata) {
            if (metadata != null) {
                this.version = metadata.version();
                this.dataFormat = metadata.dataFormatByte();
                this.code = metadata.code();
                this.message = metadata.message();
                this.tagValues = metadata.tagValues();
            }
            if (this.tagValues == null) {
                this.tagValues = TagLengthValues.create();
            }
        }

        @Override
        public Builder version(short version) {
            this.version = version;
            return this;
        }

        @Override
        public Builder dataFormat(byte dataFormat) {
            this.dataFormat = dataFormat;
            return this;
        }

        @Override
        public Builder code(int code) {
            this.code = code;
            return this;
        }

        @Override
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        @Override
        public Builder tagValues(TagLengthValues tagValues) {
            if (tagValues != null) {
                this.tagValues = tagValues;
            } else {
                this.tagValues = TagLengthValues.create();
            }
            return this;
        }

        @Override
        public ResponseMetadataV2 build() {
            return new DefaultResponseMetadataV2(this.version,
                    this.dataFormat,
                    this.code,
                    this.message,
                    this.tagValues);
        }
    }

    /**
     * 为 {@link ResponseMetadataV2} 提供序列化程序。
     *
     * @author 季聿阶
     * @since 2021-05-14
     */
    public static class Serializer implements ByteSerializer<ResponseMetadataV2> {
        /** 获取序列化程序的唯一实例。 */
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void serialize(ResponseMetadataV2 metadata, OutputStream out) throws IOException {
            out.write(Convert.toBytes(metadata.version()));
            out.write(Convert.toBytes(metadata.dataFormatByte()));
            // 是否为降级异常，该字段已经可以完全被 code 所代替。
            out.write(Convert.toBytes(false));
            out.write(Convert.toBytes(metadata.code()));
            byte[] messageBytes = Optional.ofNullable(metadata.message())
                    .map(message -> message.getBytes(StandardCharsets.UTF_8))
                    .orElse(new byte[0]);
            out.write(Convert.toBytes(messageBytes.length));
            out.write(messageBytes);
            TagLengthValues.serializer().serialize(metadata.tagValues(), out);
        }

        @Override
        public ResponseMetadataV2 deserialize(InputStream in) throws IOException {
            short readVersion = Convert.toShort(IoUtils.read(in, 2));
            byte readDataFormat = Convert.toByte(IoUtils.read(in, 1));
            // 是否为降级异常已废弃，可以完全被 code 替代
            Convert.toBoolean(IoUtils.read(in, 1));
            int readCode = Convert.toInteger(IoUtils.read(in, 4));
            ResponseMetadataV2.Builder builder =
                    ResponseMetadataV2.custom().version(readVersion).dataFormat(readDataFormat).code(readCode);
            int messageLength = Convert.toInteger(IoUtils.read(in, 4));
            byte[] messageBytes = IoUtils.read(in, messageLength);
            return builder.message(new String(messageBytes, StandardCharsets.UTF_8))
                    .tagValues(TagLengthValues.serializer().deserialize(in))
                    .build();
        }
    }
}
