/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2023. All rights reserved.
 */

package com.huawei.fitframework.broker.server;

import com.huawei.fitframework.serialization.RequestMetadata;

/**
 * 用于转发并处理请求
 *
 * @author 季聿阶
 * @since 2020-04-27
 */
public interface Dispatcher {
    /**
     * 转发处理接收到的请求。
     *
     * @param metadata 表示元数据的 {@link RequestMetadata}。
     * @param data 表示请求消息体的参数的 {@link Object}{@code []}。
     * @return 表示响应消息体的原始二进制内容的 {@link Response}。
     */
    Response dispatch(RequestMetadata metadata, Object[] data);
}
