/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.filters;

import static com.huawei.fitframework.annotation.Order.HIGHEST;

import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.http.server.DoHttpServerFilterException;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.http.server.HttpServerFilter;
import com.huawei.fit.http.server.HttpServerFilterChain;
import com.huawei.fit.jober.common.Constant;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Scope;
import com.huawei.fitframework.annotation.Value;

import java.util.Collections;
import java.util.List;

/**
 * 全局跨域问题处理Filter。
 *
 * @author 陈镕希 c00572808
 * @since 2023-06-27
 */
@Component
public class CorsFilter implements HttpServerFilter {
    private final String corsOrigin;

    /**
     * 跨域Filter默认构造方法。
     *
     * @param corsOrigin 允许跨域的网站列表信息。
     */
    public CorsFilter(@Value("${corsfilter.origin}") String corsOrigin) {
        this.corsOrigin = corsOrigin;
    }

    @Override
    public String name() {
        return "CorsFilter";
    }

    @Override
    public int priority() {
        return HIGHEST;
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
        String allowOrigin = request.headers().first("origin").orElse(this.corsOrigin);
        if (allowOrigin.endsWith(Constant.VALID_DOMAIN)) {
            response.headers().add("Access-Control-Allow-Origin", allowOrigin);
            response.headers().add("Access-Control-Allow-Credentials", "true");
            response.headers().add("Access-Control-Allow-Methods", "PUT,GET,POST,DELETE,OPTIONS,PATCH");
            response.headers().add("Access-Control-Allow-Headers", "content-type,attachment-filename");
        }
        // 允许Options请求直接返回，避免SSOFilter对Options请求进行处理随后发生401报错
        if (HttpRequestMethod.OPTIONS.equals(request.method())) {
            response.statusCode(HttpResponseStatus.NO_CONTENT.statusCode());
            response.reasonPhrase(HttpResponseStatus.NO_CONTENT.reasonPhrase());
            return;
        }
        chain.doFilter(request, response);
    }
}
