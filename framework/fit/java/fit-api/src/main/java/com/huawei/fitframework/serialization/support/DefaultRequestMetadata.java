/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 */

package com.huawei.fitframework.serialization.support;

import static com.huawei.fitframework.util.ObjectUtils.getIfNull;

import com.huawei.fitframework.serialization.ByteSerializer;
import com.huawei.fitframework.serialization.RequestMetadata;
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
 * 为 {@link RequestMetadata} 提供默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2023-11-27
 */
public class DefaultRequestMetadata implements RequestMetadata {
    private final byte dataFormat;
    private final String asyncTaskId;
    private final String genericableId;
    private final Version genericableVersion;
    private final String fitableId;
    private final Version fitableVersion;
    private final TagLengthValues tagValues;

    private DefaultRequestMetadata(byte dataFormat, String asyncTaskId, String genericableId,
            Version genericableVersion, String fitableId, Version fitableVersion, TagLengthValues tagLengthValues) {
        this.dataFormat = dataFormat;
        this.asyncTaskId = asyncTaskId;
        this.genericableId = genericableId;
        this.genericableVersion = genericableVersion;
        this.fitableId = fitableId;
        this.fitableVersion = fitableVersion;
        this.tagValues = getIfNull(tagLengthValues, TagLengthValues::create);
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
        if (another instanceof RequestMetadata) {
            return this.hashCode() == another.hashCode();
        }
        return false;
    }

    @Override
    public String toString() {
        return "{\"dataFormat\": " + this.dataFormat + ", \"genericableId\": \"" + this.genericableId
                + "\", \"genericableVersion\": \"" + this.genericableVersion + "\", \"fitableId\": " + this.fitableId
                + "\", \"fitableVersion\": \"" + this.fitableVersion + "\", \"tagValues\": " + this.tagValues + '}';
    }

    /**
     * 为创建 {@link DefaultRequestMetadata} 的实例提供构建器。
     *
     * @author 季聿阶 j00559309
     * @since 2023-11-27
     */
    public static final class Builder implements RequestMetadata.Builder {
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
         * @param metadata 表示已知的远端调用的元数据的 {@link RequestMetadata}。构建器中会以该实例中的现有数据作为初始值。
         */
        public Builder(RequestMetadata metadata) {
            if (metadata != null) {
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
        public Builder dataFormat(byte dataFormat) {
            this.dataFormat = dataFormat;
            return this;
        }

        @Override
        public Builder asyncTaskId(String asyncTaskId) {
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
        public Builder fitableVersion(Version version) {
            this.fitableVersion = version;
            return this;
        }

        @Override
        public Builder tagValues(TagLengthValues tagLengthValues) {
            this.tagValues = tagLengthValues;
            return this;
        }

        @Override
        public RequestMetadata build() {
            return new DefaultRequestMetadata(this.dataFormat,
                    this.asyncTaskId,
                    this.genericableId,
                    this.genericableVersion,
                    this.fitableId,
                    this.fitableVersion,
                    this.tagValues);
        }
    }

    /**
     * 为 {@link RequestMetadata} 提供序列化器。
     * <p>该序列化器为通用的通讯序列化器，但是不同的通讯通道应该优先使用通道特有属性，只有通道没有特有属性可用时，才考虑使用该序列化器。</p>
     *
     * @author 季聿阶 j00559309
     * @since 2023-11-27
     */
    public static class Serializer implements ByteSerializer<RequestMetadata> {
        /** 获取序列化程序的唯一实例。 */
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void serialize(RequestMetadata metadata, OutputStream out) throws IOException {
            out.write(Convert.toBytes(metadata.dataFormatByte()));
            byte[] genericableIdBytes = metadata.genericableId().getBytes(StandardCharsets.UTF_8);
            out.write(Convert.toBytes((short) genericableIdBytes.length));
            out.write(genericableIdBytes);
            Version.serializer().serialize(metadata.genericableVersion(), out);
            byte[] fitableIdBytes = metadata.fitableId().getBytes(StandardCharsets.UTF_8);
            out.write(Convert.toBytes((short) fitableIdBytes.length));
            out.write(fitableIdBytes);
            Version.serializer().serialize(metadata.fitableVersion(), out);
            TagLengthValues.serializer().serialize(metadata.tagValues(), out);
        }

        @Override
        public RequestMetadata deserialize(InputStream in) throws IOException {
            byte readDataFormatByte = Convert.toByte(IoUtils.read(in, 1));
            int readGenericableIdLength = Short.toUnsignedInt(Convert.toShort(IoUtils.read(in, 2)));
            String readGenericableId = new String(IoUtils.read(in, readGenericableIdLength), StandardCharsets.UTF_8);
            Version readGenericableVersion = Version.serializer().deserialize(in);
            int readFitableIdLength = Short.toUnsignedInt(Convert.toShort(IoUtils.read(in, 2)));
            String readFitableId = new String(IoUtils.read(in, readFitableIdLength), StandardCharsets.UTF_8);
            Version readFitableVersion = Version.serializer().deserialize(in);
            TagLengthValues readTlv = TagLengthValues.serializer().deserialize(in);
            return RequestMetadata.custom()
                    .dataFormat(readDataFormatByte)
                    .genericableId(readGenericableId)
                    .genericableVersion(readGenericableVersion)
                    .fitableId(readFitableId)
                    .fitableVersion(readFitableVersion)
                    .tagValues(readTlv)
                    .build();
        }
    }
}
