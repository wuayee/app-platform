/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.route;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 用于表示请求体中路由断言（匹配规则）对象。
 *
 * @author 张庭怿
 * @since 2024-05-16
 */
@Getter
@Setter
public class Predicate {
    private String name;

    private Map<String, Object> args;
}
