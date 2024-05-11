/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.http.client.proxy;

import com.huawei.fit.http.client.HttpClassicClientRequest;

/**
 * 表示数据目标设置器。
 *
 * @author 季聿阶
 * @since 2024-05-11
 */
public interface DestinationSetter {
    /**
     * 将数据设置进 Http 请求。
     *
     * @param request 表示 Http 请求的 {@link HttpClassicClientRequest}。
     * @param value 表示待设置的值的 {@link Object}。
     */
    void set(HttpClassicClientRequest request, Object value);
}
