/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.route;

import lombok.Data;

import java.util.ArrayList;
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
public class RouteInfo {
    private String id;

    private String model;

    private String url;

    private String path;

    /**
     * 使用路由信息构造路由更新请求体。
     *
     * @return 路由更新请求体。
     */
    public RouteUpdateRequest buildRouteConfigRequest() {
        String p = this.path == null ? "/**" : this.path;
        Map<String, Object> pathArgs = new HashMap<>();
        pathArgs.put("_genkey_0", p);

        Predicate pathPredicate = new Predicate();
        pathPredicate.setName("Path");
        pathPredicate.setArgs(pathArgs);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(pathPredicate);
        if (this.model != null && !this.model.isEmpty()) {
            Map<String, Object> modelArgs = new HashMap<>();
            modelArgs.put("model", this.model);

            Predicate modelPredicate = new Predicate();
            modelPredicate.setName("ModelPredicateFactory");
            modelPredicate.setArgs(modelArgs);

            predicates.add(modelPredicate);
        }

        return RouteUpdateRequest.builder()
                .id(this.id)
                .uri(this.url)
                .predicates(predicates)
                .build();
    }
}
