/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2023. All rights reserved.
 */

package com.huawei.fitframework.serialization.support;

import static com.huawei.fitframework.util.ObjectUtils.getIfNull;

import com.huawei.fitframework.serialization.ByteSerializer;
import com.huawei.fitframework.serialization.RequestMetadataV2;
import com.huawei.fitframework.serialization.TagLengthValues;
import com.huawei.fitframework.serialization.Version;
import com.huawei.fitframework.util.Convert;
import com.huawei.fitframework.util.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 为 {@link RequestMetadataV2} 提供默认实现。
 *
 * @author 梁济时 l00815032
 * @since 2020-11-12
 */
public class DefaultRequestMetadataV2 implements RequestMetadataV2 {
    private final short version;
    private final byte dataFormat;
    private final String asyncTaskId;
    private final String genericableId;
    private final Version genericableVersion;
    private final String fitableId;
    private final Version fitableVersion;
    private final TagLengthValues tagValues;

    private DefaultRequestMetadataV2(short version, byte dataFormat, String asyncTaskId, String genericableId,
            Version genericableVersion, String fitableId, Version fitableVersion, TagLengthValues tagLengthValues) {
        this.version = version;
        this.dataFormat = dataFormat;
        this.asyncTaskId = asyncTaskId;
        this.genericableId = genericableId;
        this.genericableVersion = genericableVersion;
        this.fitableId = fitableId;
        this.fitableVersion = fitableVersion;
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
        return Byte.toUnsignedInt(this.dataFormat);
    }

    @Override
    public String asyncTaskId() {
        return this.asyncTaskId;
    }

    @Override
    public String genericableId() {
        return this.genericableId;
    }

    @Override
    public Version genericableVersion() {
        return this.genericableVersion;
    }

    @Override
    public String fitableId() {
        return this.fitableId;
    }

    @Override
    public Version fitableVersion() {
        return this.fitableVersion;
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
        if (another instanceof RequestMetadataV2) {
            return this.hashCode() == another.hashCode();
        }
        return false;
    }

    @Override
    public String toString() {
        return "{\"version\": \"" + this.version + "\", \"dataFormat\": " + this.dataFormat + ", \"genericableId\": \""
                + this.genericableId + "\", \"genericableVersion\": \"" + this.genericableVersion
                + "\", \"fitableId\": " + this.fitableId + "\", \"fitableVersion\": \"" + this.fitableVersion
                + "\", \"tagValues\": " + this.tagValues + '}';
    }

    /**
     * 为创建 {@link DefaultRequestMetadataV2} 的实例提供构建器。
     *
     * @author 梁济时 l00815032
     * @since 2020-11-13
     */
    public static final class Builder implements RequestMetadataV2.Builder {
        private short version;
        private byte dataFormat;
        private String asyncTaskId;
        private String genericableId;
        private Version genericableVersion;
        private String fitableId;
        private Version fitableVersion;
        private TagLengthValues tagValues;

        /**
         * 使用已知的远端调用元数据初始化 {@link Builder} 类的新实例。
         *
         * @param metadata 表示已知的远端调用的元数据的 {@link RequestMetadataV2}。构建器中会以该实例中的现有数据作为初始值。
         */
        public Builder(RequestMetadataV2 metadata) {
            if (metadata != null) {
                this.version = metadata.version();
                this.dataFormat = metadata.dataFormatByte();
                this.asyncTaskId = metadata.asyncTaskId();
                this.genericableId = metadata.genericableId();
                this.genericableVersion = metadata.genericableVersion();
                this.fitableId = metadata.fitableId();
                this.fitableVersion = metadata.fitableVersion();
                this.tagValues = metadata.tagValues();
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
        public RequestMetadataV2.Builder asyncTaskId(String asyncTaskId) {
            this.asyncTaskId = asyncTaskId;
            return this;
        }

        @Override
        public Builder genericableId(String genericableId) {
            this.genericableId = genericableId;
            return this;
        }

        @Override
        public Builder genericableVersion(Version version) {
            this.genericableVersion = version;
            return this;
        }

        @Override
        public Builder fitableId(String fitableId) {
            this.fitableId = fitableId;
            return this;
        }

        @Override
        public RequestMetadataV2.Builder fitableVersion(Version version) {
            this.fitableVersion = version;
            return this;
        }

        @Override
        public RequestMetadataV2.Builder tagValues(TagLengthValues tagLengthValues) {
            this.tagValues = tagLengthValues;
            return this;
        }

        @Override
        public RequestMetadataV2 build() {
            return new DefaultRequestMetadataV2(this.version,
                    this.dataFormat,
                    this.asyncTaskId,
                    this.genericableId,
                    this.genericableVersion,
                    this.fitableId,
                    this.fitableVersion,
                    this.tagValues);
        }
    }

    /**
     * 为 {@link RequestMetadataV2} 提供序列化程序。
     *
     * @author 梁济时 l00815032
     * @since 2020-11-13
     */
    public static class Serializer implements ByteSerializer<RequestMetadataV2> {
        /** 获取序列化程序的唯一实例。 */
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void serialize(RequestMetadataV2 metadata, OutputStream out) throws IOException {
            out.write(Convert.toBytes(metadata.version()));
            out.write(Convert.toBytes(metadata.dataFormatByte()));
            Version.serializer().serialize(metadata.genericableVersion(), out);
            out.write(IoUtils.fromHexString(metadata.genericableId()));
            byte[] fitableIdBytes = metadata.fitableId().getBytes(StandardCharsets.UTF_8);
            out.write(Convert.toBytes((short) fitableIdBytes.length));
            out.write(fitableIdBytes);
            TagLengthValues.serializer().serialize(metadata.tagValues(), out);
        }

        @Override
        public RequestMetadataV2 deserialize(InputStream in) throws IOException {
            RequestMetadataV2.Builder builder = RequestMetadataV2.custom()
                    .version(Convert.toShort(IoUtils.read(in, 2)))
                    .dataFormat(Convert.toByte(IoUtils.read(in, 1)))
                    .genericableVersion(Version.serializer().deserialize(in))
                    .genericableId(IoUtils.toHexString(IoUtils.read(in, 16)));
            int fitableIdLength = Short.toUnsignedInt(Convert.toShort(IoUtils.read(in, 2)));
            byte[] fitableIdBytes = IoUtils.read(in, fitableIdLength);
            return builder.fitableId(new String(fitableIdBytes, StandardCharsets.UTF_8))
                    .tagValues(TagLengthValues.serializer().deserialize(in))
                    .build();
        }
    }
}
