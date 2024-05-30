/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 路由信息。
 *
 * @author 张庭怿
 * @since 2024-05-16
 */
@Data
@Slf4j
public class RouteInfo {
    private String id;

    private String model;

    private String url;

    private String path;

    @JsonProperty("api_key")
    private String apiKey;

    /**
     * 构造网关路由定义。
     *
     * @return {@link RouteDefinition}
     */
    public RouteDefinition buildRouteDefinition() {
        URI uri = null;
        try {
            uri = new URI(this.url);
        } catch (URISyntaxException e) {
            log.error("Failed to parse uri=" + this.url + ", error: " + e);
        }

        RouteDefinition routeDefinition = new RouteDefinition();
        routeDefinition.setUri(uri);
        routeDefinition.setId(this.id);
        routeDefinition.setPredicates(getPredicateDefinitions());

        if (this.apiKey != null && !this.apiKey.isEmpty()) {
            FilterDefinition filter = new FilterDefinition();
            filter.setName("AddRequestHeader");
            filter.addArg("name", "Authorization");
            filter.addArg("value", "Bearer " + this.apiKey);
            routeDefinition.setFilters(Collections.singletonList(filter));
            log.info("Set api key=" + this.apiKey + " for " + this.id);
        }
        return routeDefinition;
    }

    private List<PredicateDefinition> getPredicateDefinitions() {
        Map<String, String> pathArgs = new HashMap<>();
        pathArgs.put("_genkey_0", this.path == null ? "/**" : this.path);

        PredicateDefinition pathPredicate = new PredicateDefinition();
        pathPredicate.setName("Path");
        pathPredicate.setArgs(pathArgs);

        List<PredicateDefinition> predicates = new ArrayList<>();
        predicates.add(pathPredicate);
        if (this.model != null && !this.model.isEmpty()) {
            Map<String, String> modelArgs = new HashMap<>();
            modelArgs.put("model", this.model);

            PredicateDefinition modelPredicate = new PredicateDefinition();
            modelPredicate.setName("ModelPredicateFactory");
            modelPredicate.setArgs(modelArgs);
            predicates.add(modelPredicate);
        }
        return predicates;
    }
}
