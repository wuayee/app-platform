/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.jade.model.service.gateway.entity.ModelInfo;
import com.huawei.jade.model.service.gateway.entity.RouteInfo;
import com.huawei.jade.model.service.gateway.entity.RouteInfoList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关路由基础测试。
 *
 * @author 张庭怿
 * @since 2024-05-09
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {ModelGatewayApplication.class})
public class ModelGatewayRouteTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testHealthRoute() {
        webTestClient.get()
                .uri("/model-gateway/health") // 对应application.yml中health-check路由的路径
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(responseBody -> assertThat(responseBody).contains("UP"));
    }

    @Test
    void testUpdateRoutes() {
        RouteInfo routeInfo = new RouteInfo();
        routeInfo.setId("test_id");
        routeInfo.setModel("test_model");
        routeInfo.setPath("/test");
        routeInfo.setUrl("http://localhost/test_url");

        List<RouteInfo> routes = new ArrayList<>();
        routes.add(routeInfo);
        RouteInfoList requestBody = new RouteInfoList();
        requestBody.setRoutes(routes);

        webTestClient.post()
                .uri("/v1/routes")
                .bodyValue(requestBody)
                .exchange().expectStatus().isOk();

        // 使用actuator API校验路由信息是否更新
        webTestClient.get()
                .uri("/actuator/gateway/routes")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> assertThat(response).contains("test_id"));
    }

    @Test
    void testUpdateStatistics() {
        String testModel = "test_model";
        RouteInfo routeInfo = new RouteInfo();
        routeInfo.setId("test_id");
        routeInfo.setModel(testModel);
        routeInfo.setPath("/test");
        routeInfo.setUrl("http://localhost/test_url");

        List<RouteInfo> routes = new ArrayList<>();
        routes.add(routeInfo);
        RouteInfoList requestBody = new RouteInfoList();
        requestBody.setRoutes(routes);

        webTestClient.post()
                .uri("/v1/routes")
                .bodyValue(requestBody)
                .exchange().expectStatus().isOk();

        ModelInfo expectedResponse = new ModelInfo();
        expectedResponse.setModel(testModel);
        webTestClient.get()
                .uri("/v1/statistics")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ModelInfo.class).hasSize(1).contains(expectedResponse);
    }
}
