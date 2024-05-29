/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.controller;

import com.huawei.jade.model.service.gateway.entity.ModelInfo;
import com.huawei.jade.model.service.gateway.entity.RouteInfo;
import com.huawei.jade.model.service.gateway.entity.RouteInfoList;
import com.huawei.jade.model.service.gateway.service.ModelStatisticsService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.actuate.GatewayControllerEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
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
    private final GatewayControllerEndpoint gatewayControllerEndpoint;

    private Map<String, RouteInfo> currentRoutes = new ConcurrentHashMap<>();

    private ModelStatisticsService modelStatisticsService;

    public GatewayController(ModelStatisticsService service, GatewayControllerEndpoint endpoint) {
        this.modelStatisticsService = service;
        this.gatewayControllerEndpoint = endpoint;
    }

    /**
     * 更新路由信息接口。
     *
     * @param routeList {@link RouteInfoList} 路由信息列表。
     * @return 路由信息更新响应。
     */
    @PostMapping("/v1/routes")
    public ResponseEntity<String> updateRoutes(@RequestBody RouteInfoList routeList) {
        log.info("Received /v1/routes");
        if (routeList.getRoutes() == null) {
            return ResponseEntity.badRequest().body("The request body is missing 'routes' field");
        }

        List<RouteInfo> latestRoutes = routeList.getRoutes();
        this.updateCurrentRoutes(latestRoutes);
        this.deleteObsoleteRoutes(latestRoutes);

        // 手动触发路由信息刷新
        gatewayControllerEndpoint.refresh();
        return ResponseEntity.ok().build();
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
        log.info("Current statistics: " + list);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     * 更新当前路由列表。
     *
     * @param latestRoutes 最新路由列表。
     * @return 路由列表更新响应。
     */
    private ResponseEntity<String> updateCurrentRoutes(List<RouteInfo> latestRoutes) {
        for (RouteInfo routeInfo : latestRoutes) {
            if (routeInfo.getId() == null) {
                log.error("The route id is null, skip this route: " + routeInfo);
                continue;
            }

            AtomicBoolean isReceived = new AtomicBoolean(false);
            gatewayControllerEndpoint.save(routeInfo.getId(), routeInfo.buildRouteDefinition())
                    .subscribe(result -> {
                        log.info("Update " + routeInfo + ", result=" + result);
                        isReceived.set(true);
                    });
            waitUntil(isReceived::get);
            if (!isReceived.get()) {
                log.error("No result for updating " + routeInfo.getId());
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
            log.info(routeInfo.getId() + " is updated");
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
        List<String> routesToDelete = new ArrayList<>();
        for (String id : this.currentRoutes.keySet()) {
            if (latestRouteIds.contains(id)) {
                continue;
            }

            AtomicBoolean isReceived = new AtomicBoolean(false);
            this.gatewayControllerEndpoint.delete(id)
                    .subscribe(result -> {
                        log.info("Delete " + id + ", result=" + result);
                        isReceived.set(true);
                    });
            waitUntil(isReceived::get);
            if (!isReceived.get()) {
                log.error("No result for deleting " + id);
            }

            routesToDelete.add(id);
            log.info(id + " is deleted");
        }

        for (String id : routesToDelete) {
            this.currentRoutes.remove(id);
        }
        return ResponseEntity.ok().build();
    }

    private void waitUntil(Supplier<Boolean> stop) {
        final int timeout = 1000; // ms
        int time = 0;
        int step = 10;
        while (!stop.get() && time < timeout) {
            try {
                Thread.sleep(step);
            } catch (InterruptedException e) {
                log.error("Interruption: " + e);
            }
            time += step;
        }
    }
}
