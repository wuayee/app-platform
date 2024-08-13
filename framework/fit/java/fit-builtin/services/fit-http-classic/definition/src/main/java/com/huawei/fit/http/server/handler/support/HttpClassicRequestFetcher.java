/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.http.server.handler.support;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.http.server.handler.SourceFetcher;

/**
 * 表示获取整个 Http 请求的 {@link SourceFetcher}。
 *
 * @author 季聿阶
 * @since 2022-08-29
 */
public class HttpClassicRequestFetcher implements SourceFetcher {
    @Override
    public Object get(HttpClassicServerRequest request, HttpClassicServerResponse response) {
        return request;
    }
}
