/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.config;

import com.huawei.jade.model.service.gateway.ModelGatewayApplication;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.factory.RewritePathGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 网关路由配置。
 *
 * @author 张庭怿
 * @since 2024-05-28
 */
@Configuration
@Slf4j
public class GatewayRouteConfig {
    /**
     * 初始化默认路由。
     *
     * @param builder {@link RouteLocatorBuilder}
     * @return {@link RouteLocator}
     */
    @Bean
    public RouteLocator configDefaultRoutes(RouteLocatorBuilder builder) {
        RouteLocatorBuilder.Builder routeLocatorBuilder = builder.routes();
        addExtraGatewayRoute(routeLocatorBuilder);
        addModelManagerRoute(routeLocatorBuilder);
        return routeLocatorBuilder.build();
    }

    private void addExtraGatewayRoute(RouteLocatorBuilder.Builder routeLocatorBuilder) {
        String extraGatewayUrl = System.getenv("EXTRA_GATEWAY_URL");
        if (extraGatewayUrl == null || extraGatewayUrl.isEmpty()) {
            log.info("EXTRA_GATEWAY_URL is empty");
            return;
        }

        log.info("EXTRA_GATEWAY_URL=" + extraGatewayUrl);
        try {
            String path = new URI(extraGatewayUrl).getRawPath();
            if (path == null || path.isEmpty()) {
                return;
            }
            RewritePathGatewayFilterFactory.Config config = new RewritePathGatewayFilterFactory.Config();
            config.setRegexp("/(?<segment>/?.*)");
            config.setReplacement(path + "/${segment}");
            routeLocatorBuilder.route(
                    "extra-gateway",
                    r -> r.order(100) // 设置为低优先级，其余路由不匹配时才使用内部测试地址
                            .path("/**")
                            .filters(f -> f.filter(new RewritePathGatewayFilterFactory().apply(config)))
                            .uri(extraGatewayUrl)
            );
        } catch (URISyntaxException e) {
            log.error("Parse EXTRA_GATEWAY_URL failed: " + e);
        }
    }

    private void addModelManagerRoute(RouteLocatorBuilder.Builder routeLocatorBuilder) {
        String modelManagerUrl = System.getenv(ModelGatewayApplication.MODEL_MANAGER_URL);
        if (modelManagerUrl != null && !modelManagerUrl.isEmpty()) {
            routeLocatorBuilder.route(
                    "model-list",
                    r -> r.path("/v1/models").uri(modelManagerUrl)
            );
            routeLocatorBuilder.route(
                    "chat-model-list",
                    r -> r.path("/v1/chat/models").uri(modelManagerUrl)
            );
            routeLocatorBuilder.route(
                    "start-up-pipeline",
                    r -> r.path("/v1/start_up_pipeline").uri(modelManagerUrl)
            );
        } else {
            log.info(ModelGatewayApplication.MODEL_MANAGER_URL + " is empty");
        }
    }
}
