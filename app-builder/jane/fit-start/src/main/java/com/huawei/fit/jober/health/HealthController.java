/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.health;

import com.huawei.fit.http.annotation.DocumentIgnored;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fitframework.annotation.Component;

/**
 * 表示测试入口。
 *
 * @author 季聿阶 j00559309
 * @since 2023-06-06
 */
@Component
@DocumentIgnored
public class HealthController {
    private final HelloService helloService;

    public HealthController(HelloService helloService) {
        this.helloService = helloService;
    }

    /**
     * health
     *
     * @return String
     */
    @RequestMapping(method = HttpRequestMethod.GET, path = "/health")
    public String health() {
        return this.helloService.hi(System.getProperty("fit.profiles.active", "local"));
    }
}
