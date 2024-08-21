/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.filter.support;

import modelengine.fit.http.entity.TextEventStreamEntity;
import modelengine.fit.http.server.DoHttpServerFilterException;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.HttpServerFilter;
import modelengine.fit.http.server.HttpServerFilterChain;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.annotation.Scope;

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