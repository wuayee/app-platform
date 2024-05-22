/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.controller;

import com.huawei.jade.model.service.gateway.entity.ModelInfo;
import com.huawei.jade.model.service.gateway.entity.RouteInfo;
import com.huawei.jade.model.service.gateway.entity.RouteInfoList;
import com.huawei.jade.model.service.gateway.entity.RouteUpdateRequest;
import com.huawei.jade.model.service.gateway.service.ModelStatisticsService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
public class GatewayController {
    private static final String LOCAL_HOST = "http://localhost:";

    private static final String ROUTES_ENDPOINT = "/actuator/gateway/routes/";

    private static final String REFRESH_ENDPOINT = "/actuator/gateway/refresh";

    private static List<RouteInfo> routes = new ArrayList<>();

    private ModelStatisticsService modelRouteService;

    @Value("${server.port}")
    private int port;

    public GatewayController(ModelStatisticsService service) {
        this.modelRouteService = service;
    }

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

                // 如有新的模型路由信息，需要添加到统计信息
                if (!modelRouteService.getModels().containsKey(routeInfo.getModel())) {
                    ModelInfo modelInfo = new ModelInfo();
                    modelInfo.setModel(routeInfo.getModel());
                    modelRouteService.getModels().put(routeInfo.getModel(), modelInfo);
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

    /**
     * 获取模型统计信息。
     *
     * @return 模型统计信息。
     */
    @GetMapping("/v1/statistics")
    public ResponseEntity<List<ModelInfo>> getStatistics() {
        List<ModelInfo> list = new ArrayList<>();
        for (String name : modelRouteService.getModels().keySet()) {
            list.add(modelRouteService.getModels().get(name));
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
