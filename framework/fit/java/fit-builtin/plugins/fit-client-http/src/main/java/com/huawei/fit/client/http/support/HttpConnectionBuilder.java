/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.client.http.support;

import static com.huawei.fit.serialization.http.Constants.FIT_ASYNC_TASK_PATH_PATTERN;
import static com.huawei.fit.serialization.http.Constants.FIT_PATH_PATTERN;

import com.huawei.fit.client.Request;
import com.huawei.fit.http.protocol.Protocol;

/**
 * 表示 Http 链接的构建器。
 *
 * @author 季聿阶 j00559309
 * @since 2023-09-10
 */
public class HttpConnectionBuilder extends AbstractConnectionBuilder {
    /**
     * 构建长轮询链接。
     *
     * @param request 表示请求的 {@link Request}。
     * @return 表示构建出来的长轮询链接的 {@link String}。
     */
    public String buildLongPollingUrl(Request request) {
        return this.buildBaseUrl(request).append(FIT_ASYNC_TASK_PATH_PATTERN).toString();
    }

    @Override
    public Protocol protocol() {
        return Protocol.HTTP;
    }
}
