/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2023. All rights reserved.
 */

package com.huawei.fitframework.serialization;

import com.huawei.fitframework.serialization.support.DefaultResponseMetadataV2;

/**
 * 为远端服务调用提供返回值元数据。
 *
 * @author 季聿阶 j00559309
 * @since 2021-05-14
 */
public interface ResponseMetadataV2 {
    /** 表示当前所支持的最新版本。 */
    short CURRENT_VERSION = 2;

    /** 表示返回值没有错误的编码。 */
    int CODE_OK = 0;

    /**
     * 获取元数据的版本。
     *
     * @return 表示元数据版本的 {@code short}。
     */
    short version();

    /**
     * 获取消息体的格式。
     *
     * @return 表示消息体格式的字节的 {@code byte}。
     */
    byte dataFormatByte();

    /**
     * 获取消息体的格式。
     *
     * @return 表示消息体格式的 {@code int}。
     */
    int dataFormat();

    /**
     * 获取消息体的状态码。
     *
     * @return 表示状态码的 {@code int}。
     */
    int code();

    /**
     * 获取消息体的消息。
     *
     * @return 表示消息的字符串的 {@link String}。
     */
    String message();

    /**
     * 获取消息体中的扩展字段。
     *
     * @return 表示扩展字段的 {@link TagLengthValues}。
     */
    TagLengthValues tagValues();

    /**
     * 为 {@link ResponseMetadataV2} 提供构建器。
     *
     * @author 季聿阶 j00559309
     * @since 2021-05-14
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
         * 向当前构建器中设置消息体的状态码。
         *
         * @param code 表示消息体的状态码的 {@code int}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder code(int code);

        /**
         * 向当前构建器中设置消息体的消息。
         *
         * @param message 表示消息体的消息的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder message(String message);

        /**
         * 向当前构建器中设置消息体的扩展字段。
         *
         * @param tagValues 表示待设置的扩展字段的 {@link TagLengthValues}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder tagValues(TagLengthValues tagValues);

        /**
         * 构建一个远端服务调用的元数据信息。
         *
         * @return 表示包含构建器中包含的信息的远端服务调用的元数据信息的 {@link ResponseMetadataV2}。
         */
        ResponseMetadataV2 build();
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
     * @param metadata 表示构建器初始状态包含信息的 {@link ResponseMetadataV2}。
     * @return 表示用以生成远端服务调用的元数据信息默认实现的构建器的 {@link Builder}。
     */
    static Builder custom(ResponseMetadataV2 metadata) {
        return new DefaultResponseMetadataV2.Builder(metadata);
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
    static ByteSerializer<ResponseMetadataV2> serializer() {
        return DefaultResponseMetadataV2.Serializer.INSTANCE;
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
     * 将二进制序列反序列化为响应元数据。
     *
     * @param bytes 表示待反序列化的二进制序列的 {@code byte[]}。
     * @return 表示反序列化后的响应元数据的 {@link ResponseMetadataV2}。
     */
    static ResponseMetadataV2 deserialize(byte[] bytes) {
        return ByteSerializer.deserialize(serializer(), bytes);
    }
}
