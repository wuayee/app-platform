/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.client.http.support;

import static com.huawei.fit.serialization.http.Constants.FIT_PATH_PATTERN;

import com.huawei.fit.client.Address;
import com.huawei.fit.client.Request;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 表示 {@link ConnectionBuilder} 的抽象类。
 *
 * @author 季聿阶
 * @since 2024-05-07
 */
public abstract class AbstractConnectionBuilder implements ConnectionBuilder {
    private static final String GENERICABLE_ID = "{genericableId}";
    private static final String FITABLE_ID = "{fitableId}";

    @Override
    public String buildUrl(Request request) {
        StringBuilder sb = this.buildBaseUrl(request);
        sb.append(FIT_PATH_PATTERN.replace(GENERICABLE_ID, request.metadata().genericableId())
                .replace(FITABLE_ID, request.metadata().fitableId()));
        return sb.toString();
    }

    /**
     * 构建基础的访问 URL。
     *
     * @param request 表示请求的 {@link Request}。
     * @return 表示构建中的 URL 的 {@link StringBuilder}。
     */
    protected StringBuilder buildBaseUrl(Request request) {
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
}
