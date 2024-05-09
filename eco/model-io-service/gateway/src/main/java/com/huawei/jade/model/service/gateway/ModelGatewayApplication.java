/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 网关应用启动入口。
 *
 * @author 张庭怿
 * @since 2024-05-09
 */
@SpringBootApplication
public class ModelGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ModelGatewayApplication.class, args);
    }
}
