/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.client.http.support;

import com.huawei.fit.client.Address;
import com.huawei.fit.client.Request;
import com.huawei.fit.client.http.ConnectionBuilder;
import com.huawei.fitframework.serialization.RequestMetadataV2;
import com.huawei.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 表示 Http 链接的构建器。
 *
 * @author 季聿阶 j00559309
 * @since 2023-09-10
 */
public class HttpConnectionBuilder implements ConnectionBuilder {
    /** 表示通信协议。 */
    public static final String HTTP = "http";

    private static final String FIT_ASYNC_TASK_PATH_PATTERN = "/fit/async/awaitResponse";
    private static final String TASK_ID_KEY = "tid";

    @Override
    public String buildUrl(Request request) {
        StringBuilder sb = this.buildUrlBase(request);
        RequestMetadataV2 metadata = request.metadata();
        sb.append("/fit/").append(metadata.genericableId()).append("/").append(metadata.fitableId());
        return sb.toString();
    }

    /**
     * 构建长轮训的地址。
     *
     * @param request 表示请求的 {@link Request}。
     * @param taskId 表示任务标识的 {@link String}。
     * @return 表示构建后的长轮训地址的 {@link String}。
     */
    public String buildLongPollUrl(Request request, String taskId) {
        StringBuilder sb = this.buildUrlBase(request);
        sb.append(FIT_ASYNC_TASK_PATH_PATTERN).append('?').append(TASK_ID_KEY).append('=').append(taskId);
        return sb.toString();
    }

    private StringBuilder buildUrlBase(Request request) {
        Address address = request.address();
        Map<String, String> extensions = request.context().extensions();
        StringBuilder sb = new StringBuilder(this.type());
        sb.append("://").append(address.host());
        if (address.port() != this.defaultPort()) {
            sb.append(":").append(address.port());
        }
        String contextPath = extensions.getOrDefault("cluster.context-path",
                extensions.getOrDefault("http.context-path", StringUtils.EMPTY));
        sb.append(contextPath);
        return sb;
    }

    @Override
    public String type() {
        return HTTP;
    }

    /**
     * 获取默认的端口号。
     *
     * @return 表示默认的端口号的 {@code int}。
     */
    protected int defaultPort() {
        return 80;
    }
}
