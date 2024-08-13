/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.service;

import com.huawei.fit.http.annotation.DocumentIgnored;
import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.service.server.RegistryServer;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.util.MapBuilder;

import java.util.Map;

/**
 * 用于注册中心的检查。
 *
 * @author 季聿阶
 * @since 2023-06-30
 */
@Component
@RequestMapping("/fit")
public class CheckController {
    private final RegistryServer server;

    public CheckController(RegistryServer server) {
        this.server = server;
    }

    /**
     * 获取所有服务的信息。
     *
     * @return 表示所有服务信息的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     */
    @DocumentIgnored
    @GetMapping("/check")
    public Map<String, Object> getAllServices() {
        return MapBuilder.<String, Object>get()
                .put("workers", this.server.getWorkers())
                .put("applications", this.server.getApplications())
                .put("workerApplications", this.server.getWorkerApplications())
                .put("applicationMetas", this.server.getApplicationMetas())
                .build();
    }
}
