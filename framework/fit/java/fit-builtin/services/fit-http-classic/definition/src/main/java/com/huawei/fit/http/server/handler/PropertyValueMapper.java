/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.http.server.handler;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.http.server.handler.support.EmptyPropertyValueMapper;

/**
 * 表示属性值映射器。
 *
 * @author 季聿阶 j00559309
 * @since 2022-08-28
 */
@FunctionalInterface
public interface PropertyValueMapper {
    /**
     * 将 Http 请求和响应通过规则映射成为一个指定值。
     *
     * @param request 表示 Http 请求的 {@link HttpClassicServerRequest}。
     * @param response 表示 Http 响应的 {@link HttpClassicServerResponse}。
     * @return 表示映射后的指定值的 {@link Object}。
     */
    Object map(HttpClassicServerRequest request, HttpClassicServerResponse response);

    /**
     * 获取空的参数映射器。
     *
     * @return 表示空的参数映射器的 {@link PropertyValueMapper}。
     */
    static PropertyValueMapper empty() {
        return EmptyPropertyValueMapper.INSTANCE;
    }
}
