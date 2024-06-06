/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.config.HttpClientProperties;
import org.springframework.cloud.gateway.filter.NettyRoutingFilter;
import org.springframework.cloud.gateway.filter.headers.HttpHeadersFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * 路由代理配置全局过滤器。
 *
 * @author 张庭怿
 * @since 2024-06-05
 */
@Component
@Slf4j
public class ProxyFilter extends NettyRoutingFilter {
    /**
     * 路由元数据中的 http 代理字段名称。
     */
    public static final String HTTP_PROXY = "http_proxy";

    public ProxyFilter(HttpClient httpClient, ObjectProvider<List<HttpHeadersFilter>> headersFiltersProvider,
                       HttpClientProperties properties) {
        super(httpClient, headersFiltersProvider, properties);
    }

    /**
     * 为每个路由的 http 客户端配置相应的代理（从元数据中读取）。
     *
     * @param route 路由。
     * @param exchange 本次客户端与服务端的交互。
     * @return http 客户端。
     */
    @Override
    protected HttpClient getHttpClient(Route route, ServerWebExchange exchange) {
        Map<String, Object> metadata = route.getMetadata();
        String httpProxy = null;
        if (metadata.get(HTTP_PROXY) instanceof String) {
            httpProxy = (String) metadata.get(HTTP_PROXY);
        }

        if (httpProxy == null || httpProxy.isEmpty()) {
            return super.getHttpClient(route, exchange);
        }

        try {
            URI uri = new URI(httpProxy);
            String proxyHost = uri.getHost();
            int proxyPort = uri.getPort();
            String userInfo = uri.getUserInfo();

            log.info("Configure proxy for route: " + route
                    + ", proxy host=" + proxyHost + ", proxy port=" + proxyPort);
            return super.getHttpClient(route, exchange).proxy(proxy -> {
                    ProxyProvider.Builder builder = proxy.type(ProxyProvider.Proxy.HTTP)
                            .host(proxyHost)
                            .port(proxyPort);
                    if (userInfo != null && !userInfo.isEmpty()) {
                        String[] account = userInfo.split(":");
                        builder.username(account[0]);
                        builder.password(pass -> account[1]);
                    }
            });
        } catch (URISyntaxException e) {
            log.error("Failed to configure http proxy: " + httpProxy);
        }

        return super.getHttpClient(route, exchange);
    }
}
