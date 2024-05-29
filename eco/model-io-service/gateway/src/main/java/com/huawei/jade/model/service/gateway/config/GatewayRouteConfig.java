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
        String extraGatewayUrl = System.getenv("EXTRA_GATEWAY_URL");
        RouteLocatorBuilder.Builder routeLocatorBuilder = builder.routes();
        if (extraGatewayUrl != null && !extraGatewayUrl.isEmpty()) {
            RewritePathGatewayFilterFactory.Config config = new RewritePathGatewayFilterFactory.Config();
            config.setRegexp("/(?<segment>/?.*)");
            config.setReplacement("/model-gateway/${segment}");
            routeLocatorBuilder.route(
                    "extra-gateway",
                    r -> r.order(1) // 设置为低优先级，其余路由不匹配时才使用内部测试地址
                            .path("/**")
                            .filters(f -> f.filter(new RewritePathGatewayFilterFactory().apply(config)))
                            .uri(extraGatewayUrl)
            );
        } else {
            log.info("EXTRA_GATEWAY_URL is empty");
        }

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
        } else {
            log.info(ModelGatewayApplication.MODEL_MANAGER_URL + " is empty");
        }
        return routeLocatorBuilder.build();
    }
}
