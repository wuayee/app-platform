/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.controller;

import com.huawei.jade.model.service.gateway.route.RouteInfo;
import com.huawei.jade.model.service.gateway.route.RouteInfoList;
import com.huawei.jade.model.service.gateway.route.RouteUpdateRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 接收并处理路由信息更新请求。
 *
 * @author 张庭怿
 * @since 2024-05-16
 */
@Controller
@Slf4j
public class RouteUpdateController {
    private static final String LOCAL_HOST = "http://localhost:";

    private static final String ROUTES_ENDPOINT = "/actuator/gateway/routes/";

    private static final String REFRESH_ENDPOINT = "/actuator/gateway/refresh";

    private static List<RouteInfo> routes = new ArrayList<>();

    @Value("${server.port}")
    private int port;

    /**
     * 更新路由信息接口。
     *
     * @param routeList {@link RouteInfoList} 路由信息列表。
     * @return 路由信息更新响应。
     */
    @PostMapping("/v1/routes")
    public ResponseEntity<String> updateRoutes(@RequestBody RouteInfoList routeList) {
        if (routeList.getRoutes() == null) {
            return ResponseEntity.badRequest().body("The request body is missing 'routes' field");
        }

        routes = new ArrayList<>(routeList.getRoutes());
        RestTemplate restTemplate = new RestTemplate();
        for (RouteInfo routeInfo : routes) {
            log.info("Update route: " + routeInfo);
            RouteUpdateRequest request = routeInfo.buildRouteConfigRequest();
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(
                        LOCAL_HOST + port + ROUTES_ENDPOINT + routeInfo.getId(),
                        request, String.class);
                if (!response.getStatusCode().is2xxSuccessful()) {
                    log.error("Update route failed: " + routeInfo);
                }
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        }

        // 手动触发路由信息刷新
        try {
            return restTemplate.postForEntity(LOCAL_HOST + port + REFRESH_ENDPOINT,
                    null, String.class);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        }
    }
}
