/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway;

import com.huawei.jade.model.service.gateway.controller.GatewayController;
import com.huawei.jade.model.service.gateway.entity.RouteInfoList;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * 网关应用启动入口。
 *
 * @author 张庭怿
 * @since 2024-05-09
 */
@SpringBootApplication
@Slf4j
public class ModelGatewayApplication {
    /**
     * 获取初始模型路由信息的地址对应的环境变量名。
     */
    public static final String MODEL_ROUTES_URL = "MODEL_ROUTES_URL";

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ModelGatewayApplication.class, args);
        initRoutes(context);
    }

    private static void initRoutes(ApplicationContext context) {
        String managerUrl = System.getenv(MODEL_ROUTES_URL);
        if (managerUrl == null || managerUrl.isEmpty()) {
            log.error("The environment variable " + MODEL_ROUTES_URL + " is empty.");
            return;
        }

        RestTemplate restTemplate = new RestTemplate();
        RouteInfoList routeList = restTemplate.getForObject(managerUrl, RouteInfoList.class);
        if (routeList == null) {
            log.error("Failed to get initial routes.");
            return;
        }

        if (context.getBean("gatewayController") instanceof GatewayController) {
            GatewayController controller = (GatewayController) context.getBean("gatewayController");
            ResponseEntity<String> response = controller.updateRoutes(routeList);
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to add initial routes, err: " + response);
            }
        }
    }
}
