/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package modelengine.fit.http.server.handler.support;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.handler.SourceFetcher;

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
