/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.http.server.handler.support;

import static modelengine.fitframework.inspection.Validation.notBlank;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.http.server.handler.SourceFetcher;

/**
 * 表示从消息头中获取值的 {@link SourceFetcher}。
 *
 * @author 季聿阶
 * @since 2022-08-28
 */
public class HeaderFetcher implements SourceFetcher {
    private final String headerName;

    /**
     * 通过消息头名字来实例化 {@link HeaderFetcher}。
     *
     * @param headerName 表示消息头名字的 {@link String}。
     * @throws IllegalArgumentException 当 {@code headerName} 为 {@code null} 或空白字符串时。
     */
    public HeaderFetcher(String headerName) {
        this.headerName = notBlank(headerName, "The header name cannot be null.");
    }

    @Override
    public boolean isArrayAble() {
        return true;
    }

    @Override
    public Object get(HttpClassicServerRequest request, HttpClassicServerResponse response) {
        return request.headers().all(this.headerName);
    }
}
