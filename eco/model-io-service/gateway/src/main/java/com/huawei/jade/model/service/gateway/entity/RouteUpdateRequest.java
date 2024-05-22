/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 路由更新请求体。
 *
 * @author 张庭怿
 * @since 2024-05-16
 */
@Builder
@Getter
@Setter
public class RouteUpdateRequest {
    private String id;

    private List<Predicate> predicates;

    private String uri;

    private int order;
}
