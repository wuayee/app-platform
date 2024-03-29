/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.client.http.support;

import static com.huawei.fit.serialization.http.Constant.FIT_ASYNC_TASK_PATH_PATTERN;
import static com.huawei.fit.serialization.http.Constant.FIT_PATH_PATTERN;

import com.huawei.fit.client.Address;
import com.huawei.fit.client.Request;
import com.huawei.fit.http.protocol.Protocol;
import com.huawei.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 表示 Http 链接的构建器。
 *
 * @author 季聿阶 j00559309
 * @since 2023-09-10
 */
public class HttpConnectionBuilder implements ConnectionBuilder {
    private static final String GENERICABLE_ID = "{genericableId}";
    private static final String FITABLE_ID = "{fitableId}";

    @Override
    public String buildUrl(Request request) {
        StringBuilder sb = this.buildUrlBase(request);
        sb.append(FIT_PATH_PATTERN.replace(GENERICABLE_ID, request.metadata().genericableId())
                .replace(FITABLE_ID, request.metadata().fitableId()));
        return sb.toString();
    }

    @Override
    public String buildLongPollUrl(Request request) {
        return this.buildUrlBase(request).append(FIT_ASYNC_TASK_PATH_PATTERN).toString();
    }

    private StringBuilder buildUrlBase(Request request) {
        Address address = request.address();
        Map<String, String> extensions = request.context().extensions();
        StringBuilder sb = new StringBuilder(this.protocol().protocol());
        sb.append("://").append(address.host());
        if (address.port() != this.protocol().port()) {
            sb.append(":").append(address.port());
        }
        String contextPath = extensions.getOrDefault("cluster.context-path",
                extensions.getOrDefault("http.context-path", StringUtils.EMPTY));
        sb.append(contextPath);
        return sb;
    }

    @Override
    public Protocol protocol() {
        return Protocol.HTTP;
    }
}
