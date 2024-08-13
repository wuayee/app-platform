/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.http.server.handler.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.http.server.handler.SourceFetcher;

/**
 * 表示从查询参数中获取值的 {@link SourceFetcher}。
 *
 * @author 季聿阶
 * @since 2022-08-28
 */
public class QueryFetcher implements SourceFetcher {
    private final String queryKey;

    /**
     * 通过查询参数的键来实例化 {@link QueryFetcher}。
     *
     * @param queryKey 表示查询参数键的 {@link String}。
     * @throws IllegalArgumentException 当 {@code queryKey} 为 {@code null} 或空白字符串时。
     */
    public QueryFetcher(String queryKey) {
        this.queryKey = notBlank(queryKey, "The query key cannot be blank.");
    }

    @Override
    public boolean isArrayAble() {
        return true;
    }

    @Override
    public Object get(HttpClassicServerRequest request, HttpClassicServerResponse response) {
        return request.queries().all(this.queryKey);
    }
}
