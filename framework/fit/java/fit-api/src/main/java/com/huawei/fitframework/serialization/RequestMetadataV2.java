/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2023. All rights reserved.
 */

package com.huawei.fitframework.serialization;

import com.huawei.fitframework.serialization.support.DefaultRequestMetadataV2;

/**
 * 为远端服务调用提供请求元数据。
 *
 * @author 梁济时 l00815032
 * @author 季聿阶 j00559309
 * @since 2020-11-12
 */
public interface RequestMetadataV2 {
    /** 表示当前所支持的最新版本。 */
    short CURRENT_VERSION = 2;

    /**
     * 获取元数据的版本。
     *
     * @return 表示元数据版本的 {@code short}。
     */
    short version();

    /**
     * 获取消息体的格式。
     *
     * @return 表示消息体格式的 {@code byte}。
     */
    byte dataFormatByte();

    /**
     * 获取消息体的格式。
     *
     * @return 表示消息体格式的 {@code int}。
     */
    int dataFormat();

    /**
     * 获取异步请求的任务 ID。
     *
     * @return 表示异步请求任务 ID 的 {@link String}。
     */
    String asyncTaskId();

    /**
     * 获取泛服务的唯一标识。
     *
     * @return 表示泛服务唯一标识的 {@link String}。
     */
    String genericableId();

    /**
     * 获取泛服务的版本信息。
     *
     * @return 表示泛服务版本信息的 {@link Version}。
     */
    Version genericableVersion();

    /**
     * 获取泛服务实现的唯一标识。
     *
     * @return 表示泛服务实现唯一标识的 {@link String}。
     */
    String fitableId();

    /**
     * 获取泛服务实现的版本信息。
     *
     * @return 表示泛服务实现的版本信息的 {@link Version}。
     */
    Version fitableVersion();

    /**
     * 获取消息体中的扩展字段。
     *
     * @return 表示扩展字段的 {@link TagLengthValues}。
     */
    TagLengthValues tagValues();

    /**
     * 为 {@link RequestMetadataV2} 提供构建器。
     *
     * @author 梁济时 l00815032
     * @author 季聿阶 j00559309
     * @since 2020-11-13
     */
    interface Builder {
        /**
         * 向当前构建器中设置元数据的版本。
         *
         * @param version 表示元数据版本的 {@code short}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder version(short version);

        /**
         * 向当前构建器中设置消息体的格式。
         *
         * @param dataFormat 表示消息体格式的字节的 {@code byte}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder dataFormat(byte dataFormat);

        /**
         * 向当前构建器中设置异步请求的任务 ID。
         *
         * @param asyncTaskId 表示异步请求的任务 ID 的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder asyncTaskId(String asyncTaskId);

        /**
         * 向当前构建器中设置泛服务的唯一标识。
         *
         * @param genericableId 表示泛服务唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder genericableId(String genericableId);

        /**
         * 向当前构建器中设置泛服务的版本信息。
         *
         * @param version 表示泛服务版本信息的 {@link Version}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder genericableVersion(Version version);

        /**
         * 向当前构建器中设置泛服务实现的唯一标识。
         *
         * @param fitableId 表示泛服务实现唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder fitableId(String fitableId);

        /**
         * 向当前构建器中设置泛服务实现的版本信息。
         *
         * @param version 表示泛服务实现的版本信息的 {@link Version}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder fitableVersion(Version version);

        /**
         * 向当前构建器中设置消息体的扩展字段。
         *
         * @param tagLengthValues 表示待设置的扩展字段的 {@link TagLengthValues}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder tagValues(TagLengthValues tagLengthValues);

        /**
         * 构建一个远端服务调用的元数据信息。
         *
         * @return 表示包含构建器中包含的信息的远端服务调用的元数据信息的 {@link RequestMetadataV2}。
         */
        RequestMetadataV2 build();
    }

    /**
     * 返回一个构建器，用以生成远端服务调用的元数据信息默认实现的新实例。
     *
     * @return 表示用以生成远端服务调用的元数据信息默认实现的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return custom(null);
    }

    /**
     * 返回一个构建器，用以生成远端服务调用的元数据信息默认实现的新实例。
     *
     * @param metadata 表示构建器初始状态包含信息的 {@link RequestMetadataV2}。
     * @return 表示用以生成远端服务调用的元数据信息默认实现的构建器的 {@link Builder}。
     */
    static Builder custom(RequestMetadataV2 metadata) {
        return new DefaultRequestMetadataV2.Builder(metadata);
    }

    /**
     * 返回一个构建器，包含当前远端服务调用的元数据信息的数据。
     *
     * @return 表示包含当前远端服务调用的元数据信息的构建器的 {@link Builder}。
     */
    default Builder copy() {
        return custom(this);
    }

    /**
     * 返回一个序列化组件，用以对远端服务调用的元数据信息进行序列化与反序列化。
     *
     * @return 表示用以对远端服务调用的元数据信息进行序列化和反序列化的 {@link ByteSerializer}。
     */
    static ByteSerializer<RequestMetadataV2> serializer() {
        return DefaultRequestMetadataV2.Serializer.INSTANCE;
    }

    /**
     * 将当前对象序列化为二进制序列。
     *
     * @return 表示包含当前当前对象数据的二进制序列的 {@code byte[]}。
     */
    default byte[] serialize() {
        return ByteSerializer.serialize2Bytes(serializer(), this);
    }

    /**
     * 将二进制序列反序列化为请求元数据。
     *
     * @param bytes 表示待反序列化的二进制序列的 {@code byte[]}。
     * @return 表示反序列化后的请求元数据的 {@link RequestMetadataV2}。
     */
    static RequestMetadataV2 deserialize(byte[] bytes) {
        return ByteSerializer.deserialize(serializer(), bytes);
    }

    /**
     * 将 {@link RequestMetadataV2} 转换成 {@link RequestMetadata}。
     *
     * @param v2 表示待转换的元数据的 {@link RequestMetadataV2}。
     * @return 表示转换完成之后的元数据的 {@link RequestMetadata}。
     */
    static RequestMetadata convert(RequestMetadataV2 v2) {
        return RequestMetadata.custom()
                .dataFormat(v2.dataFormatByte())
                .genericableId(v2.genericableId())
                .genericableVersion(v2.genericableVersion())
                .fitableId(v2.fitableId())
                .fitableVersion(v2.fitableVersion())
                .tagValues(v2.tagValues())
                .build();
    }
}
