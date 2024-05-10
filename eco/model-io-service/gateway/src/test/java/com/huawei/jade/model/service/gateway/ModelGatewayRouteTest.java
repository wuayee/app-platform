/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

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
}
