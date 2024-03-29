/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2023. All rights reserved.
 */

package com.huawei.fitframework.broker.server;

import com.huawei.fitframework.broker.server.support.DefaultResponse;
import com.huawei.fitframework.serialization.ResponseMetadataV2;

import java.lang.reflect.Type;

/**
 * 表示 Fit 调用的原始返回值内容。
 *
 * @author 季聿阶 j00559309
 * @since 2021-05-14
 */
public interface Response {
    /**
     * 获取响应的元数据。
     *
     * @return 表示响应元数据的 {@link ResponseMetadataV2}。
     */
    ResponseMetadataV2 metadata();

    /**
     * 获取数据类型。
     *
     * @return 表示数据类型的 {@link Type}。
     */
    Type type();

    /**
     * 获取数据。
     *
     * @return 表示数据的 {@link Object}。
     */
    Object data();

    /**
     * 创建一个新的返回值数据。
     *
     * @param metadata 表示返回值中的元数据的 {@link ResponseMetadataV2}。
     * @param type 表示返回值中的数据类型的 {@link Type}。
     * @param data 表示返回值中的数据的 {@link Object}。
     * @return 表示一个新的返回值数据的 {@link Response}。
     * @throws IllegalArgumentException 当 {@code metadata} 为 {@code null} 时。
     */
    static Response create(ResponseMetadataV2 metadata, Type type, Object data) {
        return new DefaultResponse(metadata, type, data);
    }
}
