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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

    private Map<String, RouteInfo> currentRoutes = new ConcurrentHashMap<>();

    private ModelStatisticsService modelStatisticsService;

    @Value("${server.port}")
    private int gatewayPort;

    public GatewayController(ModelStatisticsService service) {
        this.modelStatisticsService = service;
    }

    /**
     * 更新路由信息接口。
     *
     * @param routeList {@link RouteInfoList} 路由信息列表。
     * @return 路由信息更新响应。
     */
    @PostMapping("/v1/routes")
    public ResponseEntity<String> updateRoutes(@RequestBody RouteInfoList routeList) {
        log.info("/v1/routes received");
        if (routeList.getRoutes() == null) {
            return ResponseEntity.badRequest().body("The request body is missing 'routes' field");
        }

        List<RouteInfo> latestRoutes = routeList.getRoutes();
        ResponseEntity<String> response = null;
        response = this.updateCurrentRoutes(latestRoutes);
        if (!response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        response = this.deleteObsoleteRoutes(latestRoutes);
        if (!response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        // 手动触发路由信息刷新
        try {
            return new RestTemplate().postForEntity(LOCAL_HOST + this.gatewayPort + REFRESH_ENDPOINT,
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
        for (String name : modelStatisticsService.getModels().keySet()) {
            list.add(modelStatisticsService.getModels().get(name));
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     * 更新当前路由列表。
     *
     * @param latestRoutes 最新路由列表。
     * @return 路由列表更新响应。
     */
    private ResponseEntity<String> updateCurrentRoutes(List<RouteInfo> latestRoutes) {
        RestTemplate restTemplate = new RestTemplate();
        for (RouteInfo routeInfo : latestRoutes) {
            if (routeInfo.getId() == null) {
                log.error("The route id is null, skip this route: " + routeInfo);
                continue;
            }

            RouteUpdateRequest request = routeInfo.buildRouteConfigRequest();
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(
                        LOCAL_HOST + this.gatewayPort + ROUTES_ENDPOINT + routeInfo.getId(),
                        request, String.class);
                if (!response.getStatusCode().is2xxSuccessful()) {
                    log.error("Update route failed: " + routeInfo);
                    return response;
                }

                // 如有新的模型路由信息，需要添加到统计信息
                if (routeInfo.getModel() != null
                        && !this.modelStatisticsService.getModels().containsKey(routeInfo.getModel())) {
                    log.info("Add new statistics for model: " + routeInfo.getModel());
                    ModelInfo modelInfo = new ModelInfo();
                    modelInfo.setModel(routeInfo.getModel());
                    this.modelStatisticsService.getModels().put(routeInfo.getModel(), modelInfo);
                }
                this.currentRoutes.put(routeInfo.getId(), routeInfo);
                log.info("Update route: " + routeInfo);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 根据最新路由列表判断当前路由列表中过时的路由，并将它们删除。
     *
     * @param latestRoutes 最新路由列表。
     * @return 路由列表删除响应。
     */
    private ResponseEntity<String> deleteObsoleteRoutes(List<RouteInfo> latestRoutes) {
        log.info("Delete routes: current routes=" + this.currentRoutes + ", latest routes=" + latestRoutes);
        Set<String> latestRouteIds = latestRoutes.stream()
                .map(RouteInfo::getId).collect(Collectors.toSet());
        RestTemplate restTemplate = new RestTemplate();
        List<String> routesToDelete = new ArrayList<>();
        for (String id : this.currentRoutes.keySet()) {
            if (latestRouteIds.contains(id)) {
                continue;
            }

            try {
                ResponseEntity<String> response =
                        restTemplate.exchange(LOCAL_HOST + this.gatewayPort + ROUTES_ENDPOINT + id,
                        HttpMethod.DELETE, RequestEntity.EMPTY, String.class);
                if (!response.getStatusCode().is2xxSuccessful()) {
                    log.error("Delete route failed: " + id);
                    return response;
                }
                routesToDelete.add(id);
                log.info("Delete route: " + id);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        }

        for (String id : routesToDelete) {
            this.currentRoutes.remove(id);
        }
        return ResponseEntity.ok().build();
    }
}
