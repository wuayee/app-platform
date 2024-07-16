/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.jade.model.service.gateway.entity.ModelInfo;
import com.huawei.jade.model.service.gateway.entity.RouteInfo;
import com.huawei.jade.model.service.gateway.entity.RouteInfoList;
import com.huawei.jade.model.service.gateway.service.ModifyModelRequestService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;

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

    @Autowired
    private Environment env;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testHealthRoute() {
        webTestClient.get()
                .uri("/actuator/health")
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

        RouteInfoList requestBody = new RouteInfoList();
        requestBody.setRoutes(Collections.singletonList(routeInfo));
        webTestClient.post()
                .uri("/v1/routes")
                .bodyValue(requestBody)
                .exchange().expectStatus().isOk();

        // 使用actuator API校验路由信息是否更新。
        webTestClient.get()
                .uri("/actuator/gateway/routes")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> assertThat(response).contains("test_id"));

        RouteInfo newRouteInfo = new RouteInfo();
        newRouteInfo.setId("new_id");
        newRouteInfo.setModel("test_model");
        newRouteInfo.setPath("/test");
        newRouteInfo.setUrl("http://localhost/test_url");
        requestBody.setRoutes(Collections.singletonList(newRouteInfo));
        webTestClient.post()
                .uri("/v1/routes")
                .bodyValue(requestBody)
                .exchange().expectStatus().isOk();

        // 使用actuator API校验路由信息是否更新。
        webTestClient.get()
                .uri("/actuator/gateway/routes")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> assertThat(response).contains("new_id"));

        // 由于第二次/v1/routes请求体未携带test_id路由，所以test_id路由应该被删除。
        webTestClient.get()
                .uri("/actuator/gateway/routes")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> assertThat(response).doesNotContain("test_id"));
    }

    @Test
    void testUpdateRoutesWithoutModel() {
        RouteInfo routeInfo = new RouteInfo();
        routeInfo.setId("test_id");
        routeInfo.setPath("/test");
        routeInfo.setUrl("http://localhost/test_url");

        RouteInfoList requestBody = new RouteInfoList();
        requestBody.setRoutes(Collections.singletonList(routeInfo));
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

        RouteInfoList requestBody = new RouteInfoList();
        requestBody.setRoutes(Collections.singletonList(routeInfo));
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

    @Test
    void testUrlWithPath() {
        RouteInfo routeInfo = new RouteInfo();
        routeInfo.setId("test_id");
        routeInfo.setUrl("http://localhost:" + env.getProperty("server.port") + "/actuator/");

        RouteInfoList requestBody = new RouteInfoList();
        requestBody.setRoutes(Collections.singletonList(routeInfo));
        webTestClient.post()
                .uri("/v1/routes")
                .bodyValue(requestBody)
                .exchange().expectStatus().isOk();

        // 预期路径可以与路由中url的路径拼接，转发后预期路径为：/actuator/health
        webTestClient.get()
                .uri("/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(responseBody -> assertThat(responseBody).contains("UP"));
    }

    @Test
    void testUpdateRouteWithMaxTokens() {
        Integer maxTokens = 100;
        RouteInfo routeInfo = new RouteInfo();
        routeInfo.setId("test_id");
        routeInfo.setModel("test_model");
        routeInfo.setPath("/test");
        routeInfo.setUrl("http://localhost/test_url");
        routeInfo.setMaxTokenSize(maxTokens);

        RouteInfoList requestBody = new RouteInfoList();
        requestBody.setRoutes(Collections.singletonList(routeInfo));
        webTestClient.post()
                .uri("/v1/routes")
                .bodyValue(requestBody)
                .exchange().expectStatus().isOk();

        if (applicationContext.getBean("modifyModelRequestService") instanceof ModifyModelRequestService) {
            ModifyModelRequestService modifyModelRequestService =
                    (ModifyModelRequestService) applicationContext.getBean("modifyModelRequestService");
            assertThat(modifyModelRequestService.getMaxTokens().get(routeInfo.getModel())).isEqualTo(maxTokens / 2);
        }
    }
}
