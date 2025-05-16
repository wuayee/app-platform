/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.common.filters;

import static modelengine.fitframework.annotation.Order.HIGHEST;

import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.DoHttpServerFilterException;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.HttpServerFilter;
import modelengine.fit.http.server.HttpServerFilterChain;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Scope;

import java.util.Collections;
import java.util.List;

/**
 * 全局跨域问题处理Filter。
 *
 * @author 陈镕希
 * @since 2023-06-27
 */
@Component
public class CorsFilter implements HttpServerFilter {
    /**
     * 跨域Filter默认构造方法。
     *
     * @param corsOrigin 允许跨域的网站列表信息。
     */
    public CorsFilter() {
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
        String allowOrigin = request.headers().first("origin").orElse("");
        response.headers().add("Access-Control-Allow-Origin", allowOrigin);
        response.headers().add("Access-Control-Allow-Credentials", "true");
        response.headers().add("Access-Control-Allow-Methods", "PUT,GET,POST,DELETE,OPTIONS,PATCH");
        response.headers().add("Access-Control-Allow-Headers", "content-type,attachment-filename,filesize,filename");
        // 允许Options请求直接返回，避免SSOFilter对Options请求进行处理随后发生401报错
        if (HttpRequestMethod.OPTIONS.equals(request.method())) {
            response.statusCode(HttpResponseStatus.NO_CONTENT.statusCode());
            response.reasonPhrase(HttpResponseStatus.NO_CONTENT.reasonPhrase());
            return;
        }
        chain.doFilter(request, response);
    }
}
