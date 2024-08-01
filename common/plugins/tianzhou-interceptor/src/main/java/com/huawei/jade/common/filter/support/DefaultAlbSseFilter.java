/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.filter.support;

import com.huawei.fit.http.entity.TextEventStreamEntity;
import com.huawei.fit.http.server.DoHttpServerFilterException;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.http.server.HttpServerFilter;
import com.huawei.fit.http.server.HttpServerFilterChain;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Order;
import com.huawei.fitframework.annotation.Scope;

import java.util.Collections;
import java.util.List;

/**
 * 表示天舟 sse 适配拦截器，ALB 服务需要加上额外的请求体。
 *
 * @author 易文渊
 * @since 2024-07-31
 */
@Component
public class DefaultAlbSseFilter implements HttpServerFilter {

    @Override
    public String name() {
        return "DefaultAlbSseFilter";
    }

    @Override
    public int priority() {
        return Order.LOW;
    }

    @Override
    public Scope scope() {
        return Scope.GLOBAL;
    }

    @Override
    public List<String> matchPatterns() {
        return Collections.singletonList("/**");
    }

    @Override
    public List<String> mismatchPatterns() {
        return Collections.emptyList();
    }

    @Override
    public void doFilter(HttpClassicServerRequest request, HttpClassicServerResponse response,
            HttpServerFilterChain chain) throws DoHttpServerFilterException {
        chain.doFilter(request, response);
        if (response.entity().isPresent() && response.entity().get() instanceof TextEventStreamEntity) {
            response.headers().add("X-Accel-Buffering", "no");
        }
    }
}