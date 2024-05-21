/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.route;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 路由信息列表（从 model-io-manager 获取）。
 *
 * @author 张庭怿
 * @since 2024-05-16
 */
@Getter
@Setter
public class RouteInfoList {
    private List<RouteInfo> routes;
}
